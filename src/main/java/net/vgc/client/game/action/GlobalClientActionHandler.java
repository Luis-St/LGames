package net.vgc.client.game.action;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.client.screen.update.ScreenUpdateFactory;
import net.vgc.game.Game;
import net.vgc.game.action.GameAction;
import net.vgc.game.action.data.gobal.IntegerData;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.data.specific.GameResultData;
import net.vgc.game.action.data.specific.StartGameData;
import net.vgc.game.action.data.specific.SyncPlayerData;
import net.vgc.game.action.handler.GameActionHandler;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class GlobalClientActionHandler implements GameActionHandler {
	
	private final Client client;
	
	public GlobalClientActionHandler(Client client) {
		this.client = client;
	}
	
	@Override
	public Game getGame() {
		return this.client.getGame();
	}
	
	@Override
	public boolean handle(GameAction<?> action) {
		if (action.type() == GameActionTypes.ROLLED_DICE && action.data() instanceof IntegerData data) {
			return this.handleRolledDice(data.getValue());
		} else if (action.type() == GameActionTypes.GAME_RESULT && action.data() instanceof GameResultData data) {
			return this.handleGameResult(data);
		} else if (action.type() == GameActionTypes.SYNC_PLAYER && action.data() instanceof SyncPlayerData data) {
			return this.handleSyncPlayer(data);
		} else if (action.type() == GameActionTypes.CANCEL_ROLL_DICE_REQUEST) {
			return this.handleCancelRollDiceRequest();
		} else if (action.type() == GameActionTypes.CAN_ROLL_DICE_AGAIN) {
			return this.handleCanRollDiceAgain();
		} else if (action.type() == GameActionTypes.UPDATE_CURRENT_PLAYER && action.data() instanceof ProfileData data) {
			return this.handleUpdateCurrentPlayer(data.getProfile());
		} else if (action.type() == GameActionTypes.START_GAME && action.data() instanceof StartGameData data) {
			return this.handleStartGame(data.getGameType(), data.getPlayerInfos());
		} else if (action.type() == GameActionTypes.CAN_SELECT_FIELD) {
			return this.handleCanSelectField();
		} else if (action.type() == GameActionTypes.CANCEL_PLAY_AGAIN_REQUEST) {
			return this.handleCancelPlayAgainRequest();
		} else if (action.type() == GameActionTypes.EXIT_GAME) {
			return this.handleExitGame();
		} else if (action.type() == GameActionTypes.STOP_GAME) {
			return this.handleStopGame();
		} else if (action.type() == GameActionTypes.ACTION_FAILED) {
			return this.handleActionFailed();
		}
		LOGGER.warn("Fail to handle action of type {}", action.type().getName());
		return false;
	}
	
	private boolean handleRolledDice(int count) {
		ScreenUpdateFactory.onDiceUpdate(count);
		LocalPlayer player = this.client.getPlayer();
		player.setCount(count);
		player.setCanRollDice(false);
		return true;
	}
	
	private boolean handleGameResult(GameResultData data) {
		ScreenUpdateFactory.onGameResultUpdate();
		return true;
	}
	
	private boolean handleSyncPlayer(SyncPlayerData data) {
		AbstractClientPlayer player = this.client.getPlayer(data.getProfile());
		if (player != null) {
			player.setPlaying(data.isPlaying());
			player.getScore().sync(data.getScore());
			LOGGER.info("Synchronize data from server of player {}", data.getProfile().getName());
			return true;
		} else {
			LOGGER.warn("Fail to synchronize data from server to player {}, since the player does not exists", data.getProfile().getName());
			return false;
		}
	}
	
	private boolean handleCancelRollDiceRequest() {
		LOGGER.info("Roll dice request was canceled from the server");
		this.client.getPlayer().setCanRollDice(false);
		return true;
	}
	
	private boolean handleCanRollDiceAgain() {
		this.client.getPlayer().setCanRollDice(true);
		return true;
	}
	
	private boolean handleUpdateCurrentPlayer(GameProfile profile) {
		boolean flag = false;
		if (this.client.getGame() == null) {
			LOGGER.warn("Fail to update the current player to {}, since there is no active game", profile.getName());
			return false;
		}
		Game game = this.client.getGame();
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player.getProfile().equals(profile)) {
				game.setPlayer(game.getPlayerFor(player));
				player.setCurrent(true);
				flag = true;
				if (player instanceof LocalPlayer localPlayer) {
					localPlayer.setCanRollDice(true);
				}
			} else {
				player.setCurrent(false);
			}
		}
		if (!flag) {
			LOGGER.warn("Fail to update the current player to {}, since the player does not exists", profile.getName());
			return false;
		}
		return true;
	}
	
	private <C extends Game> boolean handleStartGame(GameType<?, C> gameType, List<GamePlayerInfo> playerInfos) {
		if (this.client.getGame() != null) {
			LOGGER.warn("Fail to start game of type {}, since there is already a game running", gameType.getInfoName());
			return false;
		}
		C game = gameType.createClientGame(this.client, playerInfos);
		game.start();
		boolean flag = false;
		for (Player player : Util.mapList(game.getPlayers(), GamePlayer::getPlayer)) {
			player.setPlaying(true);
			if (this.client.getPlayer().getProfile().equals(player.getProfile())) {
				flag = true;
			}
		}
		if (flag) {
			gameType.openScreen(this.client, game);
			LOGGER.info("Start game {}", gameType.getInfoName());
			this.client.setGame(game);
		} else {
			LOGGER.warn("Fail to start game {}, since the local player is not in the player list of the game", gameType.getInfoName());
			this.client.setScreen(new LobbyScreen());
		}
		return true;
	}
	
	private boolean handleCanSelectField() {
		LocalPlayer player = this.client.getPlayer();
		if (player.isCurrent()) {
			player.setCanSelect(true);
			return true;
		}
		return false;
	}
	
	private boolean handleCancelPlayAgainRequest() {
		LOGGER.warn("The request to play again was canceled by the server");
		return true;
	}
	
	private boolean handleExitGame() {
		LOGGER.info("Exit the current game");
		if (this.client.getPlayer().isPlaying()) {
			this.client.getPlayer().setPlaying(false);
			this.client.getPlayer().getScore().reset();
			if (this.client.getGame() != null) {
				this.client.setGame(null);
			} else {
				LOGGER.warn("Received a exit game packet, but there is no active game");
			}
		} else {
			LOGGER.info("Received a exit game packet, but the local player is not playing a game");
		}
		this.client.setScreen(new LobbyScreen());
		return true;
	}
	
	private boolean handleStopGame() {
		LOGGER.info("Stopping the current game");
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			player.setPlaying(false);
			player.getScore().reset();
		}
		if (this.client.getGame() != null) {
			this.client.setGame(null);
		} else {
			LOGGER.warn("Received a stop game packet, but there is no active game");
		}
		this.client.setScreen(new LobbyScreen());
		return true;
	}
	
	public boolean handleActionFailed() {
		this.client.getPlayer().setCurrent(true);
		return true;
	}
	
}
