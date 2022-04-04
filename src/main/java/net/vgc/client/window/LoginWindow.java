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
import net.vgc.util.Util;

public class LoginWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static LoginWindow INSTANCE;
	
	@Nullable
	public static LoginWindow getInstance() {
		return INSTANCE;
	}
	
	protected final Stage stage;
	protected Text registrationInfoText = new Text("");
	protected Text loginGuestInfoText = new Text("");
	protected Text loginUserInfoText = new Text("");
	
	public LoginWindow(Stage stage) {
		INSTANCE = this;
		this.stage = stage;
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest((event) -> {
			INSTANCE = null;
		});
	}
	
	protected void setScene(Pane pane) {
		this.stage.setScene(new Scene(pane, 275.0, 200.0));
	}
	
	protected Pane main() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		Button registrationButton = FxUtil.makeButton("Register", () -> {
			this.setScene(this.registration());
		});
		Button loginButton = FxUtil.makeButton("Login", () -> {
			this.setScene(this.loginSelect());
		});
		pane.addColumn(0, FxUtil.makeCentered(registrationButton), FxUtil.makeCentered(loginButton));
		this.stage.setTitle("Login");
		return pane;
	}
	
	protected Pane registration() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.registrationInfoText.setVisible(false);
		this.registrationInfoText.setFill(Color.RED);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 15.0);
		TextField userNameField = new TextField();
		PasswordField passwordField = new PasswordField();
		PasswordField confirmPasswordField = new PasswordField();
		Button backButton = FxUtil.makeButton("Back", () -> {
			this.setScene(this.main());
		});
		Button registrationButton = FxUtil.makeButton("Register", () -> {
			String user = userNameField.getText();
			String password = passwordField.getText();
			String confirmPassword = confirmPasswordField.getText();
			if (user.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
			}
			if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
			}
			if (confirmPassword.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(confirmPasswordField, 750);
			}
			// TODO: registration via AccountServer
		});
		pane.addColumn(0, new Text("Username:"), new Text("Password:"), new Text("Confirm Password:"), backButton);
		pane.addColumn(1, userNameField, passwordField, confirmPasswordField, FxUtil.makeCentered(registrationButton));
		box.getChildren().addAll(pane, this.registrationInfoText);
		return box;
	}
	
	public void setRegistrationInfo(String text) {
		this.registrationInfoText.setText(text);
	}
	
	protected Pane loginSelect() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 15.0);
		Button guestButton = FxUtil.makeButton("Guest", () -> {
			this.setScene(this.loginGuest());
		});
		Button userButton = FxUtil.makeButton("User", () -> {
			this.setScene(this.loginUser());
		});
		Button backButton = FxUtil.makeButton("Back", () -> {
			this.setScene(this.main());
		});
		pane.addColumn(0, FxUtil.makeCentered(guestButton), FxUtil.makeCentered(userButton), FxUtil.makeCentered(backButton));
		return pane;
	}
	
	protected Pane loginGuest() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.loginGuestInfoText.setVisible(false);
		this.loginGuestInfoText.setFill(Color.RED);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField userNameField = new TextField();
		Button backButton = FxUtil.makeButton("Back", () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton("Login", () -> {
			String userName = userNameField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
			}
			// TODO: login guest via AccountServer
		});
		pane.addColumn(0, new Text("Name:"), backButton);
		pane.addColumn(1, userNameField, FxUtil.makeCentered(loginButton));
		box.getChildren().addAll(pane, this.loginGuestInfoText);
		return box;
	}
	
	public void setLoginGuestInfo(String text) {
		this.loginGuestInfoText.setText(text);
	}
	
	protected Pane loginUser() {
		VBox box = FxUtil.makeVerticalBox(Pos.CENTER, 10.0);
		this.loginUserInfoText.setVisible(false);
		this.loginUserInfoText.setFill(Color.RED);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField userNameField = new TextField();
		PasswordField passwordField = new PasswordField();
		Button backButton = FxUtil.makeButton("Back", () -> {
			this.setScene(this.loginSelect());
		});
		Button loginButton = FxUtil.makeButton("Login", () -> {
			String userName = userNameField.getText();
			String password = passwordField.getText();
			if (userName.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(userNameField, 750);
			}
			if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
			}
			// TODO: login user via AccountServer
		});
		pane.addColumn(0, new Text("Username:"), new Text("Password:"), backButton);
		pane.addColumn(1, userNameField, passwordField, FxUtil.makeCentered(loginButton));
		box.getChildren().addAll(pane, this.loginUserInfoText);
		return box;
	}
	
	public void setLoginUserInfo(String text) {
		this.loginUserInfoText.setText(text);
	}
	
	public void show() {
		this.setScene(this.main());
		this.stage.show();
		if (Util.isClient()) {
			Stage stage = Client.getInstance().getStage();
			this.stage.setX(stage.getX() + stage.getWidth());
			this.stage.setY(stage.getY());
		}
	}
	
	public void exit() {
		INSTANCE = null;
		this.stage.close();
	}
	
}
