package net.luis.server.network;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.dice.DiceHandler;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.type.GameType;
import net.luis.network.Connection;
import net.luis.network.packet.PacketHandler;
import net.luis.network.packet.client.ClientJoinedPacket;
import net.luis.network.packet.client.game.CancelPlayAgainGameRequestPacket;
import net.luis.network.packet.client.game.CancelPlayGameRequestPacket;
import net.luis.network.packet.client.game.ExitGamePacket;
import net.luis.network.packet.client.game.StartGamePacket;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.luis.network.packet.client.game.dice.RolledDicePacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.ClientJoinPacket;
import net.luis.network.packet.server.ClientLeavePacket;
import net.luis.network.packet.server.PlayGameRequestPacket;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.dice.RollDiceRequestPacket;
import net.luis.game.player.GameProfile;
import net.luis.server.Server;
import net.luis.server.player.ServerPlayer;
import net.luis.utility.Util;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getPacketHandler")
public class ServerPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Server server;
	private Connection connection;
	
	public ServerPacketHandler(Server server) {
		this.server = server;
	}
	
	@PacketListener(ClientJoinPacket.class)
	public void handleClientJoin(String name, UUID uuid) {
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(Utils.mapList(this.server.getPlayerList().getPlayers(), ServerPlayer::getProfile)));
	}
	
	@PacketListener(ClientLeavePacket.class)
	public void handleClientLeave(String name, UUID uuid) {
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
	@PacketListener(PlayGameRequestPacket.class)
	public <T extends Game> void handlePlayGameRequest(GameType<T> gameType, List<GameProfile> profiles) {
		MutableBoolean mutable = new MutableBoolean(false);
		List<ServerPlayer> players = this.server.getPlayerList().getPlayers(profiles).stream().filter((player) -> {
			if (player.isPlaying()) {
				mutable.setValue(true);
				return false;
			}
			return true;
		}).toList();
		if (players.size() == profiles.size()) {
			if (mutable.isFalse()) {
				if (this.server.getGame() == null) {
					T game = gameType.createGame(this.server, /*players*/Lists.newArrayList()); // TODO: find solution
					if (game != null) {
						this.server.setGame(game);
						game.start();
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							Objects.requireNonNull(player.getConnection()).send(new StartGamePacket(gameType.getId(), this.createPlayerInfos(game.getPlayers())));
						}
						Util.runDelayed("DelayedSetStartPlayer", 250, game::getStartPlayer);
					}
				} else {
					LOGGER.warn("Fail to start game {}, since there is already a running game {}", gameType.getInfoName(), this.server.getGame().getType().getInfoName());
					this.connection.send(new CancelPlayGameRequestPacket());
				}
			} else {
				LOGGER.warn("Fail to start game {}, since on player is already playing a game (this should normally not happen)", gameType.getName());
				this.connection.send(new CancelPlayGameRequestPacket());
			}
		} else {
			if (mutable.isTrue()) {
				LOGGER.warn("Fail to start game {}, since at least one selected player is already playing a game", gameType.getInfoName());
			} else {
				LOGGER.warn("Fail to start game {}, since there was an error in a player profile", gameType.getInfoName());
			}
			this.connection.send(new CancelPlayGameRequestPacket());
		}
	}
	
	private List<GamePlayerInfo> createPlayerInfos(List<GamePlayer> players) {
		List<GamePlayerInfo> playerInfos = Lists.newArrayList();
		for (GamePlayer player : players) {
			playerInfos.add(new GamePlayerInfo(player.getPlayer().getProfile(), player.getPlayerType(), Utils.mapList(player.getFigures(), GameFigure::getUUID)));
		}
		return playerInfos;
	}
	
	@PacketListener(PlayAgainGameRequestPacket.class)
	public void handlePlayAgainGameRequest(GameProfile profile) {
		ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
		if (player != null) {
			if (this.server.isAdmin(player)) {
				Game game = this.server.getGame();
				if (game != null) {
					if (!game.nextMatch()) {
						LOGGER.warn("Fail to start new match of game {}", game.getType().getInfoName());
						this.connection.send(new CancelPlayAgainGameRequestPacket());
						game.stop();
					}
				} else {
					LOGGER.warn("Fail to start new match, since there is no game running");
					this.connection.send(new CancelPlayAgainGameRequestPacket());
				}
			} else {
				LOGGER.warn("Cancel request to start a new match, since the player {} has not the required permission", profile.getName());
				this.connection.send(new CancelPlayAgainGameRequestPacket());
			}
		} else {
			LOGGER.warn("Fail to start a new match, since there was an error in a player profile {}", profile.getName());
			this.connection.send(new CancelPlayAgainGameRequestPacket());
		}
	}
	
	@PacketListener(RollDiceRequestPacket.class)
	public void handleRollDiceRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game != null) {
			GamePlayer player = game.getPlayerFor(profile);
			if (player != null) {
				if (game.isDiceGame()) {
					DiceHandler diceHandler = game.getDiceHandler();
					assert diceHandler != null;
					if (diceHandler.canRoll(player)) {
						int count;
						if (diceHandler.hasPlayerRolledDice(player)) {
							count = diceHandler.rollExclude(player, diceHandler.getLastCount(player));
						} else {
							count = diceHandler.roll(player);
						}
						LOGGER.info("Player {} rolled a {}", profile.getName(), count);
						this.connection.send(new RolledDicePacket(count));
						if (diceHandler.canRollAgain(player, count)) {
							this.connection.send(new CanRollDiceAgainPacket());
						} else if (diceHandler.canPerformGameAction(player, count)) {
							player.setRollCount(0);
							diceHandler.performGameAction(player, count);
						} else {
							player.setRollCount(0);
							game.nextPlayer(false);
						}
					} else {
						LOGGER.warn("Player {} tries to roll the dice, but he is not be able to roll it", profile.getName());
						this.connection.send(new CancelRollDiceRequestPacket());
						game.nextPlayer(false);
					}
				} else {
					LOGGER.warn("Fail to roll dice, since game {} is not a dice game", game.getType().getInfoName());
					this.connection.send(new CancelRollDiceRequestPacket());
					game.stop();
				}
			} else {
				LOGGER.warn("Fail to roll dice, since the player {} does not play game {}", profile.getName(), game.getType().getInfoName());
				this.connection.send(new CancelRollDiceRequestPacket());
				game.stop();
			}
		} else {
			LOGGER.warn("Fail to roll dice, since there is no running game");
			this.connection.send(new CancelRollDiceRequestPacket());
		}
	}
	
	@PacketListener(ExitGameRequestPacket.class)
	public void handleExitGameRequest(GameProfile profile) {
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
				Objects.requireNonNull(player.getConnection()).send(new ExitGamePacket());
			} else {
				LOGGER.warn("Fail to remove player {} from game, since there is no running game", profile.getName());
			}
		}
	}
	
}
