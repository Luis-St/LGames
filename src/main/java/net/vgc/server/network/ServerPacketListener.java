package net.vgc.server.network;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.collect.Lists;

import net.vgc.client.game.ClientGame;
import net.vgc.game.GameType;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.network.packet.client.game.CancelPlayAgainGameRequestPacket;
import net.vgc.network.packet.client.game.CancelPlayGameRequestPacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.StartGamePacket;
import net.vgc.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.vgc.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.vgc.network.packet.client.game.dice.RolledDicePacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.game.player.figure.ServerGameFigure;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class ServerPacketListener extends AbstractPacketListener {
	
	protected final DedicatedServer server;
	
	public ServerPacketListener(DedicatedServer server, NetworkSide networkSide) {
		super(networkSide);
		this.server = server;
	}
	
	public void handleClientJoin(String name, UUID uuid) {
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(this.server.getPlayerList().getPlayers()));
	}
	
	public void handleClientLeave(UUID uuid) {
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
	public <S extends ServerGame, C extends ClientGame> void handlePlayGameRequest(GameType<S, C> gameType, List<GameProfile> profiles) {
		MutableBoolean mutable = new MutableBoolean(false);
		List<ServerPlayer> players = this.server.getPlayerList().getPlayers(profiles).stream().filter((player) -> {
			if (player.isPlaying()) {
				mutable.setTrue();
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		if (players.size() == profiles.size()) {
			if (mutable.isFalse()) {
				if (this.server.getGame() == null) {
					S game = gameType.createServerGame(this.server, players);
					if (game != null) {
						this.server.setGame(game);
						game.startGame();
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							player.connection.send(new StartGamePacket(gameType, this.createPlayerInfos(game.getPlayers())));
						}
						Util.runDelayed("DelayedSetStartPlayer", 250, () -> {
							game.getStartPlayer();
						});
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
	
	protected List<GamePlayerInfo> createPlayerInfos(List<? extends ServerGamePlayer> players) {
		List<GamePlayerInfo> playerInfos = Lists.newArrayList();
		for (ServerGamePlayer player : players) {
			playerInfos.add(new GamePlayerInfo(player.getPlayer().getProfile(), player.getPlayerType(), Util.mapList(player.getFigures(), ServerGameFigure::getUUID)));
		}
		return playerInfos;
	}
	
	public void handlePlayAgainGameRequest(GameProfile profile) {
		ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
		if (player != null) {
			if (this.server.isAdmin(player)) {
				ServerGame game = this.server.getGame();
				if (game != null) {
					if (!game.nextMatch()) {
						LOGGER.warn("Fail to start new match of game {}", game.getType().getInfoName());
						this.connection.send(new CancelPlayAgainGameRequestPacket());
						game.stopGame();
					}
				} else {
					LOGGER.warn("Fail to start new match, since there is no game running");
					this.connection.send(new CancelPlayAgainGameRequestPacket());
				}
			} else {
				LOGGER.warn("Cancel request to start a new match, since the player {} has not the required permission");
				this.connection.send(new CancelPlayAgainGameRequestPacket());
			}
		} else {
			LOGGER.warn("Fail to start a new match, since there was an error in a player profile {}", profile.getName());
			this.connection.send(new CancelPlayAgainGameRequestPacket());
		}
	}
	
	public void handleRollDiceRequest(GameProfile profile) {
		ServerGame game = this.server.getGame();
		if (game != null) {
			ServerGamePlayer player = game.getPlayerFor(profile);
			if (player != null) {
				if (game.isDiceGame()) {
					DiceHandler diceHandler = game.getDiceHandler();
					if (diceHandler.canRoll(player)) {
						int lastCount = diceHandler.getLastCount(player);
						int count = lastCount > 0 ? diceHandler.rollExclude(player, lastCount) : diceHandler.roll(player);
						LOGGER.info("Player {} rolled a {}", profile.getName(), count);
						this.connection.send(new RolledDicePacket(count));
						if (diceHandler.canRollAgain(player, count)) {
							this.connection.send(new CanRollDiceAgainPacket());
						} else if (diceHandler.canPerformGameAction(player, count)) {
							// TODO: handle
						} else {
							game.nextPlayer(false);
						}
					} else {
						LOGGER.warn("Player {} tries to roll the dice, but it is not his turn", profile.getName());
						this.connection.send(new CancelRollDiceRequestPacket());
					}
				} else {
					LOGGER.warn("Fail to roll dice, since game {} is not a dice game", game.getType().getInfoName());
					this.connection.send(new CancelRollDiceRequestPacket());
				}
			} else {
				LOGGER.warn("Fail to roll dice, since the player {} does not play game {}", profile.getName(), game.getType().getInfoName());
				this.connection.send(new CancelRollDiceRequestPacket());
			}
		} else {
			LOGGER.warn("Fail to roll dice, since there is no running game");
			this.connection.send(new CancelRollDiceRequestPacket());
		}
	}
	
	public void handleExitGameRequest(GameProfile profile) {
		ServerGame game = this.server.getGame();
		if (game != null) {
			if(!game.removePlayer(game.getPlayerFor(profile), true)) {
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
				player.connection.send(new ExitGamePacket());
			} else {
				LOGGER.warn("Fail to remove player {} from game, since there is no running game", profile.getName());
			}
		}
	}
	
}
