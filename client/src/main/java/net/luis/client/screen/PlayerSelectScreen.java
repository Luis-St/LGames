package net.luis.client.screen;

import com.google.common.collect.Lists;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.fxutils.FxUtils;
import net.luis.game.player.Player;
import net.luis.game.type.GameType;
import net.luis.language.TranslationKey;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.PlayerAddPacket;
import net.luis.network.packet.client.PlayerRemovePacket;
import net.luis.network.packet.client.SyncPermissionPacket;
import net.luis.network.packet.client.game.CancelPlayGameRequestPacket;
import net.luis.network.packet.server.PlayGameRequestPacket;
import net.luis.utility.Util;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerSelectScreen extends ClientScreen {
	
	private static final Logger LOGGER = LogManager.getLogger(PlayerSelectScreen.class);
	
	private final GameType<?> gameType;
	private final ClientScreen backScreen;
	private ListView<String> playerList;
	private Button backButton;
	private Button playButton;
	
	public PlayerSelectScreen(GameType<?> gameType, ClientScreen backScreen) {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
		this.gameType = Objects.requireNonNull(gameType, "Game type must not be null");
		this.backScreen = Objects.requireNonNull(backScreen, "Back screen must not be null");
	}
	
	@Override
	public void init() {
		this.playerList = new ListView<>();
		this.playerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		for (Player player : this.client.get().getPlayerList()) {
			if (!player.isPlaying()) {
				this.playerList.getItems().add(player.getProfile().getName());
			} else {
				LOGGER.debug("Ignore player {}, since the player is already playing a game", player.getProfile().getName());
			}
		}
		this.backButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), this::handleBack);
		this.playButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.player_select.play"), this::handlePlay);
	}
	
	private void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	private void handlePlay() {
		if (Objects.requireNonNull(this.client.get().getPlayerList().getPlayer()).isAdmin()) {
			List<String> selected = this.playerList.getSelectionModel().getSelectedItems();
			int selectCount = selected.size();
			if (selectCount > this.gameType.getMaxPlayers()) {
				LOGGER.info("Unable to play game {}, since too many players {} were selected", this.gameType.getInfoName(), selectCount);
			} else if (this.gameType.getMinPlayers() > selectCount) {
				LOGGER.info("Unable to play game {}, since too few players {} were selected", this.gameType.getInfoName(), selectCount);
			} else {
				List<Player> players = Lists.newArrayList();
				for (Player player : this.client.get().getPlayerList()) {
					if (selected.contains(player.getProfile().getName())) {
						players.add(player);
					}
				}
				if (players.size() != selectCount) {
					LOGGER.warn("The player count does not match with the count of the selected players");
					this.playerList.getSelectionModel().clearSelection();
				} else {
					LOGGER.info("Send play game request to server");
					this.client.get().getPlayerList().getPlayer().getConnection().send(new PlayGameRequestPacket(this.gameType.getId(), Utils.mapList(players, Player::getProfile)));
				}
			}
		} else {
			LOGGER.warn("Tried to start a game {} but the action was canceled from server, since he has not the permission", this.gameType.getName().toLowerCase());
			this.handleBack();
		}
	}
	
	@PacketListener
	public void handlePacket(@NotNull ClientPacket clientPacket) {
		if (clientPacket instanceof PlayerAddPacket || clientPacket instanceof PlayerRemovePacket || clientPacket instanceof SyncPermissionPacket) {
			Util.runDelayed("RefreshPlayers", 250, this::refreshPlayers);
		} else if (clientPacket instanceof CancelPlayGameRequestPacket) {
			LOGGER.info("The game request was canceled by the server");
			this.handleBack();
		}
	}
	
	private void refreshPlayers() {
		this.playerList.getItems().clear();
		for (Player player : this.client.get().getPlayerList()) {
			if (!player.isPlaying()) {
				this.playerList.getItems().add(player.getProfile().getName());
			} else {
				LOGGER.debug("Ignore player {}, since the player is already playing a game", player.getProfile().getName());
			}
		}
	}
	
	@Override
	protected @NotNull Pane createPane() {
		GridPane outerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.addRow(0, FxUtils.makeDefaultVBox(this.backButton), FxUtils.makeDefaultVBox(this.playButton));
		outerPane.addColumn(0, this.playerList, innerPane);
		return outerPane;
	}
}
