package net.luis.server.network;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.dice.DiceHandler;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFigure;
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
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.server.ClientJoinPacket;
import net.luis.network.packet.server.ClientLeavePacket;
import net.luis.network.packet.server.PlayGameRequestPacket;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.dice.RollDiceRequestPacket;
import net.luis.server.Server;
import net.luis.server.player.ServerPlayer;
import net.luis.utility.Util;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ServerPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(ServerPacketHandler.class);
	
	private final Server server;
	private Connection connection;
	
	public ServerPacketHandler(@NotNull Server server) {
		this.server = server;
	}
	
	@PacketListener(ClientJoinPacket.class)
	public void handleClientJoin(String name, UUID uuid) {
		this.server.getPlayerList().addPlayer(new ServerPlayer(new GameProfile(name, uuid), this.connection));
		this.connection.send(new ClientJoinedPacket(Utils.mapList(this.server.getPlayerList().getPlayers(), Player::getProfile)));
	}
	
	@PacketListener(ClientLeavePacket.class)
	public void handleClientLeave(@NotNull String name, @NotNull UUID uuid) {
		this.server.getPlayerList().removePlayer(Objects.requireNonNull(this.server.getPlayerList().getPlayer(uuid)));
	}
	
	@PacketListener(PlayGameRequestPacket.class)
	public <T extends Game> void handlePlayGameRequest(@NotNull GameType<T> gameType, @NotNull List<GameProfile> profiles) {
		MutableBoolean mutable = new MutableBoolean(false);
		List<Player> players = profiles.stream().map(this.server.getPlayerList()::getPlayer).filter(Objects::nonNull).filter((player) -> {
			if (player.isPlaying()) {
				mutable.setValue(true);
				return false;
			}
			return true;
		}).toList();
		if (players.size() == profiles.size()) {
			if (mutable.isFalse()) {
				T game = gameType.createGame(this.server, players);
				if (game != null) {
					this.server.getGameManager().addGame(game);
					game.start();
					for (Player player : players) {
						player.setPlaying(true);
						Objects.requireNonNull(player.getConnection()).send(new StartGamePacket(gameType.getId(), this.createPlayerInfos(game.getPlayers())));
					}
					game.nextPlayer(true);
				} else {
					LOGGER.warn("Fail to start game {}, since the game was not created correctly", gameType.getName());
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
	
	private List<GamePlayerInfo> createPlayerInfos(@NotNull List<GamePlayer> players) {
		List<GamePlayerInfo> playerInfos = Lists.newArrayList();
		for (GamePlayer player : players) {
			playerInfos.add(new GamePlayerInfo(player.getPlayer().getProfile(), player.getPlayerType(), Utils.mapList(player.getFigures(), GameFigure::getUniqueId)));
		}
		return playerInfos;
	}
	
	@PacketListener(PlayAgainGameRequestPacket.class)
	public void handlePlayAgainGameRequest(@NotNull GameProfile profile) {
		Player player = this.server.getPlayerList().getPlayer(profile);
		if (player != null) {
			if (player.isAdmin()) {
				Game game = this.server.getGameManager().getGameFor(profile);
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
	public void handleRollDiceRequest(@NotNull GameProfile profile) {
		Game game = this.server.getGameManager().getGameFor(profile);
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
	public void handleExitGameRequest(@NotNull GameProfile profile) {
		Game game = this.server.getGameManager().getGameFor(profile);
		if (game != null) {
			if (!game.removePlayer(Objects.requireNonNull(game.getPlayerFor(profile)), true)) {
				LOGGER.warn("Fail to remove player {} from game {}, since the player is no playing the game", profile.getName(), game.getType().getInfoName());
			}
		} else {
			for (Player player : this.server.getPlayerList().getPlayers()) {
				if (player.isPlaying()) {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getProfile().getName());
				}
			}
			Player player = this.server.getPlayerList().getPlayer(profile);
			if (player != null) {
				Objects.requireNonNull(player.getConnection()).send(new ExitGamePacket());
			} else {
				LOGGER.warn("Fail to remove player {} from game, since there is no running game", profile.getName());
			}
		}
	}
	
}
