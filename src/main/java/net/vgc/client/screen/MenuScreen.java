package net.vgc.client.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.window.LoginWindow;
import net.vgc.language.TranslationKey;

public class MenuScreen extends Screen {
	
	protected ButtonBox loginButtonBox;
	protected ButtonBox multiplayerButtonBox;
	protected ButtonBox settingsButtonBox;
	protected VBox centerBox;
	
	@Override
	public void init() {
		this.loginButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.login"), Pos.CENTER_RIGHT, this::handleLogin);
		this.loginButtonBox.setPadding(new Insets(20.0));
		this.multiplayerButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.multiplayer"), this::handleMultiplayer);
		this.settingsButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.settings"), this::handleSettings);
		this.centerBox = FxUtil.makeVerticalBox(Pos.CENTER, 0.0);
	}
	
	@Override
	public void tick() {
		if (this.client.isLoggedIn()) {
			this.loginButtonBox.getNode().setText(TranslationKey.createAndGet("screen.menu.profile"));
		} else if (this.loginButtonBox.getNode().getText().equals(TranslationKey.createAndGet("screen.menu.profile"))) {
			this.loginButtonBox.getNode().setText(TranslationKey.createAndGet("screen.menu.login"));
		}
	}
	
	protected void handleMultiplayer() {
		this.showScreen(new MultiplayerScreen(this));
	}
	
	protected void handleSettings() {
		this.showScreen(new SettingsScreen(this));
	}
	
	protected void handleLogin() {
		if (this.client.getLoginWindow() == null)  {
			LoginWindow window = new LoginWindow(this.client, new Stage());
			window.show();
		}
	}
	
	@Override
	protected Pane createPane() {
		BorderPane border = new BorderPane();
		GridPane grid = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		grid.addColumn(0, this.multiplayerButtonBox, this.settingsButtonBox);
		this.centerBox.getChildren().add(grid);
		BorderPane.setAlignment(this.loginButtonBox, Pos.CENTER_RIGHT);
		border.setTop(this.loginButtonBox);
		BorderPane.setAlignment(this.centerBox, Pos.CENTER);
		border.setCenter(this.centerBox);
		return border;
	}
	
}
