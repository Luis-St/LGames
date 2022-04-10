package net.vgc.client.screen;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.window.LoginWindow;
import net.vgc.language.TranslationKey;

public class MenuScreen extends Screen {
	
	protected VBox singleplayerButtonBox;
	protected VBox multiplayerButtonBox;
	protected VBox settingsButtonBox;
	protected Button loginButton;
	protected VBox loginButtonBox;
	protected VBox centerBox;
	
	public MenuScreen() {
		this.width = 600;
		this.height = 600;
		this.shouldCenter = true;
	}
	
	@Override
	public void init() {
		this.singleplayerButtonBox = FxUtil.makeCentered(new Button(TranslationKey.createAndGet("screen.menu.singleplayer")), (button) -> {
			button.setOnAction(this::handleSingleplayer);
			FxUtil.setResize(button, 0.5, 0.1);
		});
		this.multiplayerButtonBox = FxUtil.makeCentered(new Button(TranslationKey.createAndGet("screen.menu.multiplayer")), (button) -> {
			button.setOnAction(this::handleMultiplayer);
			FxUtil.setResize(button, 0.5, 0.1);
		});
		this.settingsButtonBox = FxUtil.makeCentered(new Button(TranslationKey.createAndGet("screen.menu.settings")), (button) -> {
			button.setOnAction(this::handleSettings);
			FxUtil.setResize(button, 0.5, 0.1);
		});
		this.loginButton = FxUtil.makeButton(this.client.isLoggedIn() ? TranslationKey.createAndGet("screen.menu.profile") : TranslationKey.createAndGet("screen.menu.login"), () -> {
			this.handleLogin();
		});
		this.loginButtonBox = FxUtil.make(this.loginButton, Pos.CENTER_RIGHT);
		this.loginButtonBox.setPadding(new Insets(20.0));
		this.centerBox = FxUtil.makeVerticalBox(Pos.CENTER, 0.0);
	}
	
	@Override
	public void tick() {
		if (this.client.isLoggedIn()) {
			this.loginButton.setText(TranslationKey.createAndGet("screen.menu.profile"));
		}
	}

	@Override
	public Scene show() {
		BorderPane border = new BorderPane();
		GridPane grid = FxUtil.makeGrid(Pos.CENTER, 6.0, 10.0);
		grid.addColumn(0, this.singleplayerButtonBox, this.multiplayerButtonBox, this.settingsButtonBox);
		this.centerBox.getChildren().add(grid);
		BorderPane.setAlignment(this.loginButtonBox, Pos.CENTER_RIGHT);
		border.setTop(this.loginButtonBox);
		BorderPane.setAlignment(this.centerBox, Pos.CENTER);
		border.setCenter(this.centerBox);
		return new Scene(border, this.width, this.height);
	}
	
	protected void handleSingleplayer(ActionEvent event) { // TODO: impl
		LOGGER.debug("Singleplayer");
	}
	
	protected void handleMultiplayer(ActionEvent event) { // TODO: impl
		LOGGER.debug("Multiplayer");
	}
	
	protected void handleSettings(ActionEvent event) { // TODO: impl
		LOGGER.debug("Settings");
	}
	
	protected void handleLogin() {
		LoginWindow loginWindow = new LoginWindow(new Stage());
		loginWindow.show();
	}

}
