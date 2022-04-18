package net.vgc.client.screen;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.InputPane;
import net.vgc.language.TranslationKey;

public class MultiplayerScreen extends Screen {
	
	protected final Screen backScreen;
	protected InputPane hostInputPane;
	protected InputPane portInputPane;
	protected VBox connectButtonBox;
	protected VBox backButtonBox;
	
	public MultiplayerScreen(Screen backScreen) {
		this.backScreen = backScreen;
		this.width = 600;
		this.height = 600;
		this.shouldCenter = true;
	}
	
	@Override
	public void init() {
		this.hostInputPane = new InputPane(TranslationKey.createAndGet("screen.multiplayer.server_host"));
		this.portInputPane = new InputPane(TranslationKey.createAndGet("screen.multiplayer.server_port"));
		this.connectButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.multiplayer.connect"), this::handleBack);
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
	}
	
	protected void handleConnect() {
		LOGGER.debug("Connect");
	}
	
	protected void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	@Override
	protected Pane createPane() {
		GridPane outerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.addColumn(0, this.connectButtonBox, this.backButtonBox);
		outerPane.addColumn(0, this.hostInputPane, this.portInputPane, innerPane);
		return outerPane;
	}

}
