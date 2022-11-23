package net.vgc.client.screen;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.action.data.specific.PlayRequestData;
import net.vgc.game.action.type.ActionTypes;
import net.vgc.game.type.GameType;
import net.vgc.language.TranslationKey;

public class PlayerSelectScreen extends Screen {
	
	private final GameType<?, ?> gameType;
	private final Screen backScreen;
	private ListView<String> playerList;
	private ButtonBox backButtonBox;
	private ButtonBox playButtonBox;
	
	public PlayerSelectScreen(GameType<?, ?> gameType, Screen backScreen) {
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
			} else {
				LOGGER.debug("Ignore player {}, since the player is already playing a game", player.getProfile().getName());
			}
		}
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
		this.playButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.player_select.play"), this::handlePlay);
	}
	
	private void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	private void handlePlay() {
		if (this.client.getPlayer().isAdmin()) {
			List<String> selected = this.playerList.getSelectionModel().getSelectedItems();
			int selectCount = selected.size();
			if (selectCount > this.gameType.getMaxPlayers()) {
				LOGGER.info("Unable to play game {}, since too many players {} were selected", this.gameType.getInfoName(), selectCount);
			} else if (this.gameType.getMinPlayers() > selectCount) {
				LOGGER.info("Unable to play game {}, since too few players {} were selected", this.gameType.getInfoName(), selectCount);
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
					ActionTypes.PLAY_REQUEST.send(this.client.getServerHandler(), new PlayRequestData(this.gameType, players.stream().map(AbstractClientPlayer::getProfile).collect(Collectors.toList())));
				}
			}
		} else {
			LOGGER.warn("Tried to start a game {} but the action was canceled from server, since he has not the permission", this.gameType.getName().toLowerCase());
			this.handleBack();
		}
	}
	
	/*@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			Util.runDelayed("RefreshPlayers", 250, this::refreshPlayers);
		} else if (clientPacket instanceof CancelPlayGameRequestPacket) {
			LOGGER.info("The game request was canceled by the server");
			this.handleBack();
		}
	}
	
	private void refreshPlayers() {
		this.playerList.getItems().clear();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (!player.isPlaying()) {
				this.playerList.getItems().add(player.getProfile().getName());
			} else {
				LOGGER.debug("Ignore player {}, since the player is already playing a game", player.getProfile().getName());
			}
		}
	}*/
	
	@Override
	protected Pane createPane() {
		GridPane outerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.addRow(0, this.backButtonBox, this.playButtonBox);
		outerPane.addColumn(0, this.playerList, innerPane);
		return outerPane;
	}
	
}
