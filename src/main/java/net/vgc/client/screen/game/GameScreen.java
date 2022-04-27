package net.vgc.client.screen.game;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.screen.Screen;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientScreenPacket;

public abstract class GameScreen extends Screen {
	
	protected final Menu gameMenu = this.createGameMenu();
	protected final Menu playerMenu = this.createPlayerMenu();
	
	public GameScreen() {
		
	}
	
	@Override
	public abstract void handlePacket(ClientScreenPacket clientPacket);
	
	protected abstract Menu createGameMenu();
	
	protected Menu createPlayerMenu() {
		return new Menu(TranslationKey.createAndGet("server.window.players"));
	}
	
	protected List<Menu> getMenus() {
		return Lists.newArrayList(this.gameMenu, this.playerMenu);
	}
	
	protected abstract Pane createGame();
	
	@Override
	protected final Pane createPane() {
		this.refreshPlayers();
		return new VBox(new MenuBar(this.getMenus().toArray(Menu[]::new)), this.createGame());
	}
	
	protected void refreshPlayers() {
		this.playerMenu.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player instanceof LocalPlayer) {
				if (player.isAdmin()) {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.local_player_admin", player.getGameProfile().getName())));
				} else {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.local_player", player.getGameProfile().getName())));
				}
			} else {
				if (player.isAdmin()) {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.remote_player_admin", player.getGameProfile().getName())));
				} else {
					this.playerMenu.getItems().add(new MenuItem(TranslationKey.createAndGet("screen.game.remote_player", player.getGameProfile().getName())));
				}
			}
		}
	}

}
