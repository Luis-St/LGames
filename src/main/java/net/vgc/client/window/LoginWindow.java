package net.vgc.client.window;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.client.Client;
import net.vgc.client.fx.FxAnimationUtil;
import net.vgc.client.fx.FxUtil;
import net.vgc.common.LoginType;
import net.vgc.common.Result;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.server.account.PlayerAccount;
import net.vgc.util.Util;

public class LoginWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static LoginWindow INSTANCE;
	
	@Nullable
	public static LoginWindow getInstance() {
		return INSTANCE;
	}
	
	protected final Client client = Client.getInstance();
	protected final Stage stage;
	protected Text registrationInfoText = new Text("");
	protected Text loginGuestInfoText = new Text("");
	protected Text loginUserInfoText = new Text("");
	
	public LoginWindow(Stage stage) {
		INSTANCE = this;
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
		pane.addColumn(0, FxUtil.makeCentered(registrationButton), FxUtil.makeCentered(loginButton));
		return pane;
	}
	
	protected Pane profile() { // REWORK -> show password only if user -> log out button
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 15.0);
		PlayerAccount account = this.client.getAccount();
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.username")), new Text(TranslationKey.createAndGet("window.login.password")));
		pane.addColumn(1, new Text(account.getName()));
		account.getPassword(pane);
		return pane;
	}
	
	protected Pane registration() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.registrationInfoText.setVisible(false);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 15.0);
		TextField userNameField = new TextField();
		PasswordField passwordField = new PasswordField();
		PasswordField confirmPasswordField = new PasswordField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.client.isLoggedIn() ? this.profile() : this.main());
		});
		Button registrationButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.register"), () -> {
			String userName = userNameField.getText();
			String password = passwordField.getText();
			String confirmPassword = confirmPasswordField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
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
				this.connectAndSend(new ClientLoginPacket(LoginType.REGISTRATION, userName, password));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.username")), new Text(TranslationKey.createAndGet("window.login.password")), new Text(TranslationKey.createAndGet("window.login.confirm_password")), backButton);
		pane.addColumn(1, userNameField, passwordField, confirmPasswordField, FxUtil.makeCentered(registrationButton));
		box.getChildren().addAll(pane, this.registrationInfoText);
		return box;
	}
	
	protected Pane loginSelect() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 15.0);
		Button guestButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.guest"), () -> {
			this.setScene(this.loginGuest());
		});
		Button userButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.user"), () -> {
			this.setScene(this.loginUser());
		});
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.client.isLoggedIn() ? this.profile() : this.main());
		});
		pane.addColumn(0, FxUtil.makeCentered(guestButton), FxUtil.makeCentered(userButton), FxUtil.makeCentered(backButton));
		return pane;
	}
	
	protected Pane loginGuest() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.loginGuestInfoText.setVisible(false);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField userNameField = new TextField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			String userName = userNameField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
				LOGGER.info("Guest name is not set");
			} else {
				this.connectAndSend(new ClientLoginPacket(LoginType.GUEST_LOGIN, userName, ""));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.name")), backButton);
		pane.addColumn(1, userNameField, FxUtil.makeCentered(loginButton));
		box.getChildren().addAll(pane, this.loginGuestInfoText);
		return box;
	}
	
	protected Pane loginUser() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.loginUserInfoText.setVisible(false);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField userNameField = new TextField();
		PasswordField passwordField = new PasswordField();
		Button backButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.back"), () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			String userName = userNameField.getText();
			String password = passwordField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
				LOGGER.info("Username is not set");
			} else if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
				LOGGER.info("Password is not set");
			} else {
				this.connectAndSend(new ClientLoginPacket(LoginType.USER_LOGIN, userName, password));
			}
		});
		pane.addColumn(0, new Text(TranslationKey.createAndGet("window.login.username") + ":"), new Text(TranslationKey.createAndGet("window.login.password") + ":"), backButton);
		pane.addColumn(1, userNameField, passwordField, FxUtil.makeCentered(loginButton));
		box.getChildren().addAll(pane, this.loginUserInfoText);
		return box;
	}
	
	protected void connectAndSend(Packet<?> packet) {
		try {
			this.client.connectAccount();
		} catch (Exception e) {
			LOGGER.warn("Fail to connect to account server, since: {}", e.getMessage());
		}
		
		Util.runDelayed("LoginPacketSend", 1000, () -> {
			Connection connection = this.client.getAccountConnection();
			if (this.client.isAccountConnected()) {
				connection.send(packet);
			} else {
				LOGGER.warn("Unable to send Packet of type {} to account server, since connection is closed", packet.getClass().getSimpleName());
			}
		});
	}
	
	public void setInfo(LoginType loginType, Result result, String info) {
		switch (loginType) {
			case REGISTRATION -> this.setinfo(this.registrationInfoText, result, info);
			case USER_LOGIN -> this.setinfo(this.loginUserInfoText, result, info);
			case GUEST_LOGIN -> this.setinfo(this.loginGuestInfoText, result, info);
			case UNKNOWN -> LOGGER.warn("Fail to set login info: {}", info);
		}
	}
	
	protected void setinfo(Text text, Result result, String info) {
		text.setText(info);
		text.setFill(result == Result.FAILED ? Color.RED : Color.GREEN);
		text.setVisible(true);
		Util.runDelayed("InfoTextInvisible", 5000, () -> {
			text.setVisible(false);
		});
	}
	
	public void handleLoggedIn(LoginType loginType) {
		this.stage.setTitle(TranslationKey.createAndGet("screen.menu.profile"));
		this.setScene(this.profile());
	}
	
	public void show() {
		boolean loggedIn = this.client.isLoggedIn();
		this.setScene(loggedIn ? this.profile() : this.main());
		this.stage.setTitle(loggedIn ? TranslationKey.createAndGet("screen.menu.profile") : TranslationKey.createAndGet("screen.menu.login"));
		this.stage.show();
		Stage stage = this.client.getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
	}
	
	public void exit() {
		this.stage.close();
	}
	
	protected void close() {
		INSTANCE = null;
	}
	
}
