package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.luis.client.window.LoginWindow;
import net.luis.fx.ButtonBox;
import net.luis.fxutils.FxUtils;
import net.luis.language.TranslationKey;
import net.luis.utility.Tickable;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class MenuScreen extends ClientScreen implements Tickable {
	
	private Button loginButton;
	private ButtonBox multiplayerButtonBox;
	private ButtonBox settingsButtonBox;
	private VBox centerBox;
	
	public MenuScreen() {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
	}
	
	@Override
	public void init() {
		this.loginButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.menu.login"), this::handleLogin);
		this.multiplayerButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.multiplayer"), this::handleMultiplayer);
		this.settingsButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.menu.settings"), this::handleSettings);
		this.centerBox = FxUtils.makeVBox(Pos.CENTER, 0.0);
	}
	
	@Override
	public void tick() {
		if (this.client.getAccountManager().isLoggedIn()) {
			this.loginButton.setText(TranslationKey.createAndGet("screen.menu.profile"));
		} else if (this.loginButton.getText().equals(TranslationKey.createAndGet("screen.menu.profile"))) {
			this.loginButton.setText(TranslationKey.createAndGet("screen.menu.login"));
		}
	}
	
	private void handleMultiplayer() {
		this.showScreen(new MultiplayerScreen(this));
	}
	
	private void handleSettings() {
		/*this.showScreen(new SettingsScreen(this));*/
	}
	
	private void handleLogin() {
		if (this.client.getAccountManager().getLoginWindow() == null) {
			LoginWindow window = new LoginWindow(this.client, new Stage());
			window.show();
		}
	}
	
	@Override
	protected @NotNull Pane createPane() {
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
