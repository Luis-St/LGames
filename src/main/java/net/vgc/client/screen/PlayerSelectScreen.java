package net.vgc.client.screen;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.vgc.Constans;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.Game;
import net.vgc.language.TranslationKey;
import net.vgc.network.packet.client.ClientPlayerAddPacket;
import net.vgc.network.packet.client.ClientPlayerRemovePacket;
import net.vgc.network.packet.client.ClientScreenPacket;
import net.vgc.network.packet.client.SyncPermissionPacket;

public class PlayerSelectScreen extends Screen {
	
	protected final Game game;
	protected final Screen backScreen;
	protected ListView<String> playerList;
	protected ButtonBox backButtonBox;
	protected ButtonBox playButtonBox;
	
	public PlayerSelectScreen(Game game, Screen backScreen) {
		this.game = game;
		this.backScreen = backScreen;
	}
	
	@Override
	public void init() {
		this.playerList = new ListView<>();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			this.playerList.getItems().add(player.getGameProfile().getName());
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
			if (selectCount > this.game.getMaxPlayers()) {
				LOGGER.info("Unable to play game {}, since too many players were selected", this.game);
			} else if (this.game.getMinPlayers() > selectCount) {
				LOGGER.info("Unable to play game {}, since too few players were selected", this.game);
			} else {
				List<AbstractClientPlayer> players = Lists.newArrayList();
				for (AbstractClientPlayer player : this.client.getPlayers()) {
					if (selected.contains(player.getGameProfile().getName())) {
						players.add(player);
					}
				}
				if (players.size() != selectCount) {
					LOGGER.warn("The player count does not match with the count of the selected players");
					this.playerList.getSelectionModel().clearSelection();
				} else {
					LOGGER.debug("Play game");
				}
			}
		} else {
			LOGGER.warn("Player {} tried to start a game but the action was canceled, since he has not the permission", this.client.getPlayer().getGameProfile().getName());
			if (Constans.IDE) {
				this.handleBack();
			} else {
				this.showScreen(new MenuScreen());
				this.client.getServerHandler().close();
			}
		}
	}
	
	@Override
	public void handlePacket(ClientScreenPacket clientPacket) {
		if (clientPacket instanceof ClientPlayerAddPacket || clientPacket instanceof ClientPlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			this.refreshPlayers();
		}
	}
	
	public void refreshPlayers() {
		this.playerList.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			this.playerList.getItems().add(player.getGameProfile().getName());
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
