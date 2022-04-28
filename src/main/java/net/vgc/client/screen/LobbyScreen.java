package net.vgc.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.screen.game.GameScreen;
import net.vgc.game.Games;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.PlayerAddPacket;
import net.vgc.network.packet.client.PlayerRemovePacket;
import net.vgc.network.packet.client.ClientScreenPacket;
import net.vgc.network.packet.client.SyncPermissionPacket;

public class LobbyScreen extends GameScreen {
	
	protected ButtonBox tttButtonBox;
	
	public LobbyScreen() {
		
	}
	
	@Override
	public void init() {
		this.tttButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.lobby.ttt"), this::handleTTT);
	}
	
	protected void handleTTT() {
		this.showScreen(new PlayerSelectScreen(Games.TIC_TAC_TOE, this));
	}
	
	@Override
	public void handlePacket(ClientScreenPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			this.refreshPlayers();
		}
	}
	
	@Override
	protected Menu createGameMenu() {
		Menu menu = new Menu(TranslationKey.createAndGet("screen.lobby.game"));
		CustomMenuItem leaveItem = new CustomMenuItem();
		leaveItem.setContent(FxUtil.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), () -> {
			this.client.removePlayer();
			this.showScreen(new MenuScreen());
		}));
		menu.getItems().add(leaveItem);
		return menu;
	}

	@Override
	protected Pane createGame() {
		GridPane gridPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		gridPane.addRow(0, this.tttButtonBox);
		return gridPane;
	}

}
