package net.vgc.server.game.action;

import org.jetbrains.annotations.Nullable;

import net.vgc.game.Game;
import net.vgc.game.action.GameAction;
import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.gobal.IntegerData;
import net.vgc.game.action.data.gobal.ProfileData;
import net.vgc.game.action.handler.GameActionHandler;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.Connection;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;

/**
 *
 * @author Luis-st
 *
 */

public class GlobalServerActionHandler implements GameActionHandler {
	
	private final DedicatedServer server;
	private Connection connection;
	
	public GlobalServerActionHandler(DedicatedServer server) {
		this.server = server;
	}
	
	@Nullable
	@Override
	public Game getGame() {
		return this.server.getGame();
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public boolean handle(GameAction<?> action) {
		if (action.type() == GameActionTypes.PLAY_AGAIN_REQUEST && action.data() instanceof ProfileData data) {
			return this.handlePlayAgainRequest(data.getProfile());
		} else if (action.type() == GameActionTypes.DICE_REQUEST && action.data() instanceof ProfileData data) {
			return this.handleDiceRequest(data.getProfile());
		} else if (action.type() == GameActionTypes.EXIT_GAME_REQUEST && action.data() instanceof ProfileData data) {
			return this.handleExitGameRequest(data.getProfile());
		}
		LOGGER.warn("Fail to handle action of type {}", action.type().getName());
		return false;
	}
	
	private boolean handlePlayAgainRequest(GameProfile profile) {
		ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
		if (player == null) {
			LOGGER.warn("Fail to start a new match, since there was an error in a player profile {}", profile.getName());
			GameActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
			return false;
		}
		if (!this.server.isAdmin(player)) {
			LOGGER.warn("Cancel request to start a new match, since the player {} has not the required permission");
			GameActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
			return false;
		}
		Game game = this.server.getGame();
		if (game == null) {
			LOGGER.warn("Fail to start new match, since there is no game running");
			GameActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
			return false;
		}
		if (!game.nextMatch()) {
			LOGGER.warn("Fail to start new match of game {}", game.getType().getInfoName());
			GameActionTypes.CANCEL_PLAY_AGAIN_REQUEST.send(this.connection, new EmptyData());
			game.stop();
			return false;
		}
		return true;
	}
	
	private boolean handleDiceRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game == null) {
			LOGGER.warn("Fail to roll dice, since there is no running game");
			GameActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
			return false;
		}
		GamePlayer player = game.getPlayerFor(profile);
		if (player == null) {
			LOGGER.warn("Fail to roll dice, since the player {} does not play game {}", profile.getName(), game.getType().getInfoName());
			GameActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
			game.stop();
			return false;
		}
		if (!game.isDiceGame()) {
			LOGGER.warn("Fail to roll dice, since game {} is not a dice game", game.getType().getInfoName());
			GameActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
			game.stop();
			return false;
		}
		DiceHandler diceHandler = game.getDiceHandler();
		if (!diceHandler.canRoll(player)) {
			LOGGER.warn("Player {} tries to roll the dice, but he is not be able to roll it", profile.getName());
			GameActionTypes.CANCEL_ROLL_DICE_REQUEST.send(this.connection, new EmptyData());
			game.nextPlayer(false);
			return false;
		}
		int count = diceHandler.hasPlayerRolledDice(player) ? diceHandler.rollExclude(player, diceHandler.getLastCount(player)) : diceHandler.roll(player);
		LOGGER.info("Player {} rolled a {}", profile.getName(), count);
		GameActionTypes.ROLLED_DICE.send(this.connection, new IntegerData(count));
		if (diceHandler.canRollAgain(player, count)) {
			GameActionTypes.CAN_ROLL_DICE_AGAIN.send(this.connection, new EmptyData());
		} else if (diceHandler.canPerformGameAction(player, count)) {
			player.setRollCount(0);
			diceHandler.performGameAction(player, count);
		} else {
			player.setRollCount(0);
			game.nextPlayer(false);
		}
		return true;
	}
	
	private boolean handleExitGameRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game != null) {
			if (!game.removePlayer(game.getPlayerFor(profile), true)) {
				LOGGER.warn("Fail to remove player {} from game {}, since the player is no playing the game", profile.getName(), game.getType().getInfoName());
			}
		} else {
			for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
				if (player.isPlaying()) {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getProfile().getName());
				}
			}
			ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
			if (player != null) {
				GameActionTypes.EXIT_GAME.send(this.connection, new EmptyData());
			} else {
				LOGGER.warn("Fail to remove player {} from game, since there is no running game", profile.getName());
			}
		}
		return true;
	}
	
}
