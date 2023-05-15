package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.luis.client.player.LocalPlayer;
import net.luis.fxutils.FxUtils;
import net.luis.game.player.Player;
import net.luis.game.type.GameTypes;
import net.luis.language.TranslationKey;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.PlayerAddPacket;
import net.luis.network.packet.client.PlayerRemovePacket;
import net.luis.network.packet.client.SyncPermissionPacket;
import net.luis.utility.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class LobbyScreen extends ClientScreen {
	
	private Menu playerMenu;
	private Menu gameMenu;
	private Button tttButton;
	private Button ludoButton;
	private Button wins4Button;
	
	public LobbyScreen() {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
	}
	
	private boolean isAdmin() {
		return Objects.requireNonNull(this.client.get().getPlayerList().getPlayer()).isAdmin();
	}
	
	@Override
	public void init() {
		this.playerMenu = new Menu(TranslationKey.createAndGet("server.window.players"));
		this.gameMenu = new Menu(TranslationKey.createAndGet("screen.lobby.game"));
		CustomMenuItem leaveItem = new CustomMenuItem();
		leaveItem.setContent(FxUtils.makeButton(TranslationKey.createAndGet("screen.lobby.leave"), () -> {
			this.client.get().getPlayerList().removePlayer();
			this.showScreen(new MenuScreen());
		}));
		this.gameMenu.getItems().add(leaveItem);
		this.tttButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.lobby.ttt"), this::handleTTT);
		this.tttButton.setDisable(!this.isAdmin());
		this.ludoButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.lobby.ludo"), this::handleLudo);
		this.ludoButton.setDisable(!this.isAdmin());
		this.wins4Button = FxUtils.makeButton(TranslationKey.createAndGet("screen.lobby.4wins"), this::handleWins4);
		this.wins4Button.setDisable(!this.isAdmin());
	}
	
	private void handleTTT() {
		if (this.isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.TIC_TAC_TOE, this));
		}
	}
	
	private void handleLudo() {
		if (this.isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.LUDO, this));
		}
	}
	
	private void handleWins4() {
		if (this.isAdmin()) {
			this.showScreen(new PlayerSelectScreen(GameTypes.WINS_4, this));
		}
	}
	
	@PacketListener
	public void handlePacket(@NotNull ClientPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			Util.runDelayed("RefreshPlayers", 250, this::refreshPlayers);
			this.tttButton.setDisable(!this.isAdmin());
			this.ludoButton.setDisable(!this.isAdmin());
			this.wins4Button.setDisable(!this.isAdmin());
		}
	}
	
	private void refreshPlayers() {
		this.playerMenu.getItems().clear();
		for (Player player : this.client.get().getPlayerList()) {
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
	protected @NotNull Pane createPane() {
		GridPane gridPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		gridPane.addRow(0, FxUtils.makeDefaultVBox(this.tttButton), FxUtils.makeDefaultVBox(this.ludoButton), FxUtils.makeDefaultVBox(this.wins4Button));
		this.refreshPlayers();
		return new VBox(new MenuBar(this.playerMenu, this.gameMenu), gridPane);
	}
}
