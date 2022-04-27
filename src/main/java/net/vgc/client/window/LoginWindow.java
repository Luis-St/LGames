package net.vgc.client.window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.client.Client;
import net.vgc.client.fx.Box;
import net.vgc.client.fx.FxAnimationUtil;
import net.vgc.client.fx.FxUtil;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.ConnectionHandler;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.network.packet.account.ClientLogoutPacket;
import net.vgc.util.Util;

public class LoginWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final Client client;
	protected final Stage stage;
	
	public LoginWindow(Client client, Stage stage) {
		this.client = client;
		this.client.setLoginWindow(this);
		this.stage = stage;
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest((event) -> {
			this.close();
		});
	}
	
	protected void setScene(Pane pane) {
		this.stage.setScene(new Scene(pane, 275.0, 200.0));
	}
	
	protected Pane main() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		Button registrationButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.register"), () -> {
			this.setScene(this.registration());
		});
		Button loginButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			this.setScene(this.loginSelect());
		});
		Button closeButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.close"), () -> {
			this.exit();
		});
		pane.addColumn(0, new Box<>(registrationButton), new Box<>(loginButton), new Box<>(closeButton));
		return pane;
	}
	
	protected Pane profile() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		PlayerAccount account = this.client.getAccount();
		pane.addRow(0, new Text(TranslationKey.createAndGet("window.login.username")), new Text(account.getName()));
		if (!account.isGuest()) {
			pane.addRow(1, new Text(TranslationKey.createAndGet("window.login.password")));
			account.displayPassword(pane);
		}
		Button logoutButton = FxUtil.makeButton(TranslationKey.createAndGet("window.logout.logout"), () -> {
			ConnectionHandler handler = this.client.getAccountHandler();
			Connection connection = handler.getConnection();
			if (connection.isConnected()) {
				connection.send(new ClientLogoutPacket(account));
			}
		});
		pane.add(new Box<>(logoutButton), 1, 2);
		return pane;
	}
	
	protected Pane registration() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		PasswordField confirmPasswordField = new PasswordField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.client.isLoggedIn() ? this.profile() : this.main());
		});
		Button registrationButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.register"), () -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			String confirmPassword = confirmPasswordField.getText();
			if (username.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(usernameField, 750);
				LOGGER.info("Username is not set");
			} else if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
				LOGGER.info("Password is not set");
			} else if (confirmPassword.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(confirmPasswordField, 750);
				LOGGER.info("Password was not confirmed");
			} else if (!password.trim().equals(confirmPassword.trim())) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
				FxAnimationUtil.makeEmptyText(confirmPasswordField, 750);
				LOGGER.info("Passwords do not match");
			} else {
				this.connectAndSend(new ClientLoginPacket(LoginType.REGISTRATION, username, password));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.username")), new Text(TranslationKey.createAndGet("window.login.password")), new Text(TranslationKey.createAndGet("window.login.confirm_password")), backButton);
		pane.addColumn(1, usernameField, passwordField, confirmPasswordField, new Box<>(registrationButton));
		return pane;
	}
	
	protected Pane loginSelect() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		Button userButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.user"), () -> {
			this.setScene(this.loginUser());
		});
		Button guestButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.guest"), () -> {
			this.setScene(this.loginGuest());
		});
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.client.isLoggedIn() ? this.profile() : this.main());
		});
		pane.addColumn(0, new Box<>(userButton), new Box<>(guestButton), new Box<>(backButton));
		return pane;
	}
	
	protected Pane loginGuest() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField usernameField = new TextField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			String userName = usernameField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(usernameField, 750);
				LOGGER.info("Guest name is not set");
			} else {
				this.connectAndSend(new ClientLoginPacket(LoginType.GUEST_LOGIN, userName, ""));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.name")), backButton);
		pane.addColumn(1, usernameField, new Box<>(loginButton));
		return pane;
	}
	
	protected Pane loginUser() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			if (username.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(usernameField, 750);
				LOGGER.info("Username is not set");
			} else if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
				LOGGER.info("Password is not set");
			} else {
				this.connectAndSend(new ClientLoginPacket(LoginType.USER_LOGIN, username, password));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.username") + ":"), new Text(TranslationKey.createAndGet("window.login.password") + ":"), backButton);
		pane.addColumn(1, usernameField, passwordField, new Box<>(loginButton));
		return pane;
	}
	
	protected void connectAndSend(Packet<?> packet) {
		ConnectionHandler handler = this.client.getAccountHandler();
		try {
			handler.connect(this.client.getAccountHost(), this.client.getAccountPort());
		} catch (Exception e) {
			LOGGER.warn("Fail to connect to account server", e);
		}
		Util.runDelayed("DelayedPacketSender", 1000, () -> {
			Connection connection = handler.getConnection();
			if (handler.isConnected()) {
				connection.send(packet);
			} else {
				LOGGER.warn("Unable to send Packet of type {} to account server, since connection is closed", packet.getClass().getSimpleName());
			}
		});
	}
	
	public void handleLoggedIn(LoginType loginType) {
		this.stage.setTitle(TranslationKey.createAndGet("screen.menu.profile"));
		this.setScene(this.profile());
	}
	
	public void handleLoggedOut() {
		this.stage.setTitle(TranslationKey.createAndGet("screen.menu.login"));
		this.setScene(this.main());
	}
	
	public void show() {
		boolean loggedIn = this.client.isLoggedIn();
		this.setScene(loggedIn ? this.profile() : this.main());
		this.stage.setTitle(loggedIn ? TranslationKey.createAndGet("screen.menu.profile") : TranslationKey.createAndGet("screen.menu.login"));
		Stage stage = this.client.getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
		this.stage.show();
	}
	
	public void exit() {
		this.stage.close();
		this.close();
	}
	
	protected void close() {
		this.client.setLoginWindow(null);
		if (!this.client.isLoggedIn()) {
			this.client.getAccountHandler().close();
		}
	}
	
}
