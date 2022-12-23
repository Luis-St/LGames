package net.vgc.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.luis.fxutils.FxUtils;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.window.LoginWindow;
import net.vgc.language.TranslationKey;

/**
 *
 * @author Luis-st
 *
 */

public class MenuScreen extends Screen {
	
	private Button loginButton;
	private ButtonBox multiplayerButtonBox;
	private ButtonBox settingsButtonBox;
	private VBox centerBox;
	
	@Override
	public void init() {
		this.loginButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.menu.login"), this::handleLogin);
		this.multiplayerButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.multiplayer"), this::handleMultiplayer);
		this.settingsButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.settings"), this::handleSettings);
		this.centerBox = FxUtils.makeVBox(Pos.CENTER, 0.0);
	}
	
	@Override
	public void tick() {
		if (this.client.isLoggedIn()) {
			this.loginButton.setText(TranslationKey.createAndGet("screen.menu.profile"));
		} else if (this.loginButton.getText().equals(TranslationKey.createAndGet("screen.menu.profile"))) {
			this.loginButton.setText(TranslationKey.createAndGet("screen.menu.login"));
		}
	}
	
	private void handleMultiplayer() {
		this.showScreen(new MultiplayerScreen(this));
	}
	
	private void handleSettings() {
		this.showScreen(new SettingsScreen(this));
	}
	
	private void handleLogin() {
		if (this.client.getLoginWindow() == null) {
			LoginWindow window = new LoginWindow(this.client, new Stage());
			window.show();
		}
	}
	
	@Override
	protected Pane createPane() {
		BorderPane border = new BorderPane();
		GridPane grid = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		grid.addColumn(0, this.multiplayerButtonBox, this.settingsButtonBox);
		this.centerBox.getChildren().add(grid);
		BorderPane.setAlignment(this.loginButton, Pos.CENTER_RIGHT);
		border.setTop(FxUtils.makeVBox(Pos.CENTER_RIGHT, 20.0, this.loginButton));
		BorderPane.setAlignment(this.centerBox, Pos.CENTER);
		border.setCenter(this.centerBox);
		return border;
	}
	
}
