package net.vgc.server.network;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;

import net.vgc.game.Game;
import net.vgc.game.GameResult;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.ttt.TTTGame;
import net.vgc.game.ttt.TTTType;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.game.ttt.map.TTTResultLine;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.network.packet.client.game.CancelPlayAgainGameRequestPacket;
import net.vgc.network.packet.client.game.CancelPlayGameRequestPacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.GameScoreUpdatePacket;
import net.vgc.network.packet.client.game.StartTTTGamePacket;
import net.vgc.network.packet.client.game.TTTGameResultPacket;
import net.vgc.network.packet.client.game.UpdateTTTGamePacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedPlayerList;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class ServerPacketListener extends AbstractPacketListener {
	
	protected final DedicatedServer server;
	
	public ServerPacketListener(DedicatedServer server, NetworkSide networkSide) {
		super(networkSide);
		this.server = server;
	}
	
	public void handleClientJoin(String name, UUID uuid) {
		this.checkSide();
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(this.server.getPlayerList().getPlayers()));
	}
	
	public void handleClientLeave(UUID uuid) {
		this.checkSide();
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
	public void handlePlayGameRequest(GameType<?> gameType, List<GameProfile> profiles) {
		this.checkSide();
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
					Game game = gameType.createNewGame(players);
					if (game != null) {
						this.server.setGame(game);
						ServerPlayer startPlayer = game.getStartPlayer();
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							if (gameType == GameTypes.TIC_TAC_TOE && game instanceof TTTGame tttGame) {
								player.connection.send(new StartTTTGamePacket(tttGame.getPlayerType(player), startPlayer, players));
							}
						}
					}
				} else {
					LOGGER.warn("Fail to start game {}, since there is already a running game {}", gameType.getName().toLowerCase(), this.server.getGame().getType().getName());
					this.connection.send(new CancelPlayGameRequestPacket());
				}
			} else {
				LOGGER.warn("Fail to start game {}, since on player is already playing a game (this should normally not happen)", gameType.getName());
				this.connection.send(new CancelPlayGameRequestPacket());
			}
		} else {
			if (mutable.isTrue()) {
				LOGGER.warn("Fail to start game {}, since on player is already playing a game", gameType.getName());
			} else {
				LOGGER.warn("Fail to start game {}, since there was an error in a player profile", gameType.getName());
			}
			this.connection.send(new CancelPlayGameRequestPacket());
		}
	}
	
	public void handlePlayAgainGameRequest(GameProfile profile) {
		ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
		if (player != null) {
			if (this.server.isAdmin(player)) {
				Game game = this.server.getGame();
				if (game != null) {
					if (!game.nextMatch()) {
						LOGGER.warn("Fail to start new match of game {}", game.getType().getName().toLowerCase());
						this.connection.send(new CancelPlayAgainGameRequestPacket());
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
	
	public void handlePressTTTField(GameProfile profile, int vMap, int hMap) {
		DedicatedPlayerList playerList = this.server.getPlayerList();
		Game game = this.server.getGame();
		if (game != null) {
			if (game instanceof TTTGame tttGame) {
				ServerPlayer currentPlayer = playerList.getPlayer(profile);
				if (tttGame.getCurrentPlayer() != null && tttGame.getCurrentPlayer().equals(currentPlayer)) {
					if (currentPlayer.isPlaying()) {
						TTTMap oldMap = tttGame.getMap();
						TTTMap newMap = tttGame.handle(vMap, hMap, false);
						if (!oldMap.equals(newMap)) {
							if (newMap.hasResult()) {
								LOGGER.info("Handle result of game {}", tttGame.getType().getName().toLowerCase());
								playerList.broadcastAll(tttGame.getPlayers(), new UpdateTTTGamePacket(newMap));
								Util.runDelayed("DelayedPacketSender", 250, () -> {
									TTTResultLine resultLine = newMap.getResultLine();
									for (ServerPlayer player : tttGame.getPlayers()) {
										ServerPlayer enemyPlayer = tttGame.getEnemiesFor(player).get(0);
										GameResult result = newMap.getResult(tttGame.getPlayerType(player));
										Connection connection = player.connection;
										if (result == GameResult.DRAW) {
											connection.send(new TTTGameResultPacket(GameProfile.EMPTY, TTTType.NO, GameProfile.EMPTY, TTTType.NO, TTTResultLine.EMPTY));
											tttGame.getScore().getScore(player.getProfile()).increaseDraw();
											connection.send(new GameScoreUpdatePacket(tttGame.getScore()));
										} else if (result == GameResult.WIN) {
											connection.send(new TTTGameResultPacket(player.getProfile(), tttGame.getPlayerType(player), enemyPlayer.getProfile(), tttGame.getPlayerType(enemyPlayer), resultLine));
											tttGame.getScore().getScore(player.getProfile()).increaseWin();
											connection.send(new GameScoreUpdatePacket(tttGame.getScore()));
										} else if (result == GameResult.LOSE) {
											connection.send(new TTTGameResultPacket(enemyPlayer.getProfile(), tttGame.getPlayerType(enemyPlayer), player.getProfile(), tttGame.getPlayerType(player), resultLine));
											tttGame.getScore().getScore(player.getProfile()).increaseLose();
											connection.send(new GameScoreUpdatePacket(tttGame.getScore()));
										} else {
											LOGGER.warn("Fail to handle result {} of game {}", result.getName(), tttGame.getType().getName().toLowerCase());
										}
									}
								});
							} else {
								tttGame.nextPlayer();
								playerList.broadcastAll(tttGame.getPlayers(), new UpdateTTTGamePacket(newMap));
							}
						} else {
							LOGGER.info("Field map will not be synced to the clients, since there are no changes");
						}
					} else {
						LOGGER.warn("Fail to handle press {} field packet for player {}, since the player is not playing a game", tttGame.getType().getName().toLowerCase(), currentPlayer.getProfile().getName());
					}
				} else {
					LOGGER.warn("Player {} tries to change the {} field in map at pos {}:{} to {}, but it is not his turn", profile.getName(), tttGame.getType().getName(), vMap, hMap, tttGame.getPlayerType(currentPlayer));
				}
			} else {
				LOGGER.warn("Fail to handle press {} field packet, since the current game is {}", GameTypes.TIC_TAC_TOE.getName().toLowerCase(), game.getType().getName().toLowerCase());
			}
		} else {
			LOGGER.warn("Fail to handle press {} field packet, since there is no running game", GameTypes.TIC_TAC_TOE.getName().toLowerCase());
		}
	}
	
	public void handleExitGameRequest(GameProfile profile) {
		Game game = this.server.getGame();
		if (game != null) {
			ServerPlayer player = this.server.getPlayerList().getPlayer(profile);
			if(!game.removePlayer(player, true)) {
				LOGGER.warn("Fail to remove player {} from game {}, since the player is no playing the game", profile.getName(), game.getType().getName().toLowerCase());
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
