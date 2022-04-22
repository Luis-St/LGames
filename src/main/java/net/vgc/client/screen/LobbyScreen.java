package net.vgc.client.screen;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPlayerAddPacket;
import net.vgc.network.packet.client.ClientPlayerRemovePacket;
import net.vgc.network.packet.client.ClientScreenPacket;

public class LobbyScreen extends Screen {
	
	protected HBox menuBox;
	protected MenuBar playerMenuBar;
	protected MenuBar gameMenuBar;
	protected Menu playerMenu;
	protected Menu gameMenu;
	
	public LobbyScreen() {
		
	}
	
	@Override
	public void init() {
		this.menuBox = new HBox();
		this.playerMenuBar = new MenuBar();
		this.playerMenuBar.setPrefWidth(1000.0);
		this.playerMenu = new Menu(TranslationKey.createAndGet("server.window.players"));
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player instanceof LocalPlayer) {
				this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.lobby.local_player", player.getGameProfile().getName())));
			} else {
				this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.lobby.remote_player", player.getGameProfile().getName())));
			}
		}
		this.playerMenuBar.getMenus().add(this.playerMenu);
		this.gameMenuBar = new MenuBar();
		this.gameMenu = new Menu(TranslationKey.createAndGet("screen.lobby.game"));
		CustomMenuItem leaveItem = new CustomMenuItem();
		leaveItem.setContent(FxUtil.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), () -> {
			this.client.removePlayer();
			this.showScreen(new MenuScreen());
		}));
		this.gameMenu.getItems().add(leaveItem);
		this.gameMenuBar.getMenus().addAll(this.gameMenu);
	}
	
	@Override
	public void handlePacket(ClientScreenPacket clientPacket) {
		if (clientPacket instanceof ClientPlayerAddPacket || clientPacket instanceof ClientPlayerRemovePacket) {
			this.refreshPlayers();
		}
	}
	
	protected void refreshPlayers() {
		this.playerMenu.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player instanceof LocalPlayer) {
				this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.lobby.local_player", player.getGameProfile().getName())));
			} else {
				this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.lobby.remote_player", player.getGameProfile().getName())));
			}
		}
	}
	
	@Override
	protected Pane createPane() {
		this.menuBox.getChildren().addAll(this.playerMenuBar, this.gameMenuBar);
		return new VBox(this.menuBox);
	}

}
