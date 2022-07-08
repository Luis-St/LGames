package net.vgc.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.screen.game.GameScreen;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.PlayerAddPacket;
import net.vgc.network.packet.client.PlayerRemovePacket;
import net.vgc.network.packet.client.SyncPermissionPacket;
import net.vgc.game.GameTypes;
import net.vgc.util.Util;

public class LobbyScreen extends GameScreen {
	
	private Menu playerMenu;
	private Menu gameMenu;
	private ButtonBox tttButtonBox;
	private ButtonBox ludoButtonBox;
	private ButtonBox wins4ButtonBox;
	
	public LobbyScreen() {
		
	}
	
	@Override
	public void init() {
		this.playerMenu = new Menu(TranslationKey.createAndGet("server.window.players"));
		this.gameMenu = new Menu(TranslationKey.createAndGet("screen.lobby.game"));
		CustomMenuItem leaveItem = new CustomMenuItem();
		leaveItem.setContent(FxUtil.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), () -> {
			this.client.removePlayer();
			this.showScreen(new MenuScreen());
		}));
		this.gameMenu.getItems().add(leaveItem);
		this.tttButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.lobby.ttt"), this::handleTTT);
		this.tttButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
		this.ludoButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.lobby.ludo"), this::handleLudo);
		this.ludoButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
		this.wins4ButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.lobby.4wins"), this::handleWins4);
		this.wins4ButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
	}
	
	private void handleTTT() {
		if (this.client.getPlayer().isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.TIC_TAC_TOE, this));
		}
	}
	
	private void handleLudo() {
		if (this.client.getPlayer().isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.LUDO, this));
		}
	}
	
	private void handleWins4() {
		if (this.client.getPlayer().isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.WINS_4, this));
		}
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			Util.runDelayed("RefreshPlayers", 250, this::refreshPlayers);
			this.tttButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
			this.ludoButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
			this.wins4ButtonBox.getNode().setDisable(!this.client.getPlayer().isAdmin());
		}
	}
	
	private void refreshPlayers() {
		this.playerMenu.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player instanceof LocalPlayer) {
				if (player.isAdmin()) {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.local_player_admin", player.getProfile().getName())));
				} else {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.local_player", player.getProfile().getName())));
				}
			} else {
				if (player.isAdmin()) {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.remote_player_admin", player.getProfile().getName())));
				} else {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.remote_player", player.getProfile().getName())));
				}
			}
		}
	}
	
	@Override
	protected Pane createPane() {
		GridPane gridPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		gridPane.addRow(0, this.tttButtonBox, this.ludoButtonBox, this.wins4ButtonBox);
		this.refreshPlayers();
		return new VBox(new MenuBar(this.playerMenu, this.gameMenu), gridPane);
	}

}
