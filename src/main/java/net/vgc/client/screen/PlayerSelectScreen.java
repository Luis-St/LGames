package net.vgc.client.screen;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.GameType;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.PlayerAddPacket;
import net.vgc.network.packet.client.PlayerRemovePacket;
import net.vgc.network.packet.client.SyncPermissionPacket;
import net.vgc.network.packet.client.game.CancelPlayGameRequestPacket;
import net.vgc.network.packet.server.PlayGameRequestPacket;
import net.vgc.util.Util;

public class PlayerSelectScreen extends Screen {
	
	protected final GameType<?> gameType;
	protected final Screen backScreen;
	protected ListView<String> playerList;
	protected ButtonBox backButtonBox;
	protected ButtonBox playButtonBox;
	
	public PlayerSelectScreen(GameType<?> gameType, Screen backScreen) {
		this.gameType = gameType;
		this.backScreen = backScreen;
	}
	
	@Override
	public void init() {
		this.playerList = new ListView<>();
		this.playerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (!player.isPlaying()) {
				this.playerList.getItems().add(player.getProfile().getName());
			}
		}
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
		this.playButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.player_select.play"), this::handlePlay);
	}
	
	protected void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	protected void handlePlay() {
		if (this.client.getPlayer().isAdmin()) {
			List<String> selected = this.playerList.getSelectionModel().getSelectedItems();
			int selectCount = selected.size();
			if (selectCount > this.gameType.getMaxPlayers()) {
				LOGGER.info("Unable to play game {}, since too many players {} were selected", this.gameType, selectCount);
			} else if (this.gameType.getMinPlayers() > selectCount) {
				LOGGER.info("Unable to play game {}, since too few players {} were selected", this.gameType, selectCount);
			} else {
				List<AbstractClientPlayer> players = Lists.newArrayList();
				for (AbstractClientPlayer player : this.client.getPlayers()) {
					if (selected.contains(player.getProfile().getName())) {
						players.add(player);
					}
				}
				if (players.size() != selectCount) {
					LOGGER.warn("The player count does not match with the count of the selected players");
					this.playerList.getSelectionModel().clearSelection();
				} else {
					LOGGER.debug("Send play game request to server");
					this.client.getServerHandler().send(new PlayGameRequestPacket(this.gameType, players));
				}
			}
		} else {
			LOGGER.warn("Player {} tried to start a game but the action was canceled, since he has not the permission", this.client.getPlayer().getProfile().getName());
			this.handleBack();
		}
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			Util.runDelayed("RefreshPlayers", 250, this::refreshPlayers);
		} else if (clientPacket instanceof CancelPlayGameRequestPacket) {
			LOGGER.info("The game request was canceled by the server");
			this.handleBack();
		}
	}
	
	protected void refreshPlayers() {
		this.playerList.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (!player.isPlaying()) {
				this.playerList.getItems().add(player.getProfile().getName());
			}
		}
	}

	@Override
	protected Pane createPane() {
		GridPane outerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.addRow(0, this.backButtonBox, this.playButtonBox);
		outerPane.addColumn(0, this.playerList, innerPane);
		return outerPane;
	}

}
