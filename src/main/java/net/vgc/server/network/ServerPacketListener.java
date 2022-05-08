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
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.network.packet.client.game.CancelPlayGameRequestPacket;
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
	
	public void handlePlayGameRequest(GameType<?> gameType, List<GameProfile> gameProfiles) {
		this.checkSide();
		MutableBoolean mutable = new MutableBoolean(false);
		List<ServerPlayer> players = this.server.getPlayerList().getPlayers(gameProfiles).stream().filter((player) -> {
			if (player.isPlaying()) {
				mutable.setTrue();
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		if (players.size() == gameProfiles.size()) {
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
				LOGGER.warn("Fail to start game {}, since there was an error in a game profile", gameType.getName());
			}
			this.connection.send(new CancelPlayGameRequestPacket());
		}
	}
	
	public void handlePressTTTField(GameProfile gameProfile, int vMap, int hMap) {
		DedicatedPlayerList playerList = this.server.getPlayerList();
		Game game = this.server.getGame();
		if (game != null) {
			if (game instanceof TTTGame tttGame) {
				ServerPlayer currentPlayer = playerList.getPlayer(gameProfile);
				if (tttGame.getCurrentPlayer() != null && tttGame.getCurrentPlayer().equals(currentPlayer)) {
					if (currentPlayer.isPlaying()) {
						TTTMap oldMap = tttGame.getMap();
						TTTMap newMap = tttGame.handle(vMap, hMap, false);
						if (!oldMap.equals(newMap)) {
							if (newMap.hasResult()) {
								LOGGER.info("Handle result of game {}", tttGame.getType().getName().toLowerCase());
								playerList.broadcastAll(tttGame.getPlayers(), new UpdateTTTGamePacket(newMap, tttGame.getCurrentPlayer().getGameProfile()));
								Util.runDelayed("DelayedPacketSender", 250, () -> {
									TTTResultLine resultLine = newMap.getResultLine();
									for (ServerPlayer player : tttGame.getPlayers()) {
										ServerPlayer enemyPlayer = tttGame.getEnemiesFor(player).get(0);
										GameResult result = newMap.getResult(tttGame.getPlayerType(player));
										if (result == GameResult.DRAW) {
											player.connection.send(new TTTGameResultPacket(GameProfile.EMPTY, TTTType.NO, GameProfile.EMPTY, TTTType.NO, TTTResultLine.EMPTY));
										} else if (result == GameResult.WIN) {
											player.connection.send(new TTTGameResultPacket(player.getGameProfile(), tttGame.getPlayerType(player), enemyPlayer.getGameProfile(), tttGame.getPlayerType(enemyPlayer), resultLine));
										} else if (result == GameResult.LOSE) {
											player.connection.send(new TTTGameResultPacket(enemyPlayer.getGameProfile(), tttGame.getPlayerType(enemyPlayer), player.getGameProfile(), tttGame.getPlayerType(player), resultLine));
										} else {
											LOGGER.warn("Fail to handle result {} of game {}", result.getName(), tttGame.getType().getName().toLowerCase());
										}
									}
								});
							} else {
								tttGame.nextPlayer();
								playerList.broadcastAll(tttGame.getPlayers(), new UpdateTTTGamePacket(newMap, tttGame.getCurrentPlayer().getGameProfile()));
							}
						} else {
							LOGGER.info("Field map will not be synced to the clients, since there are no changes");
						}
					} else {
						LOGGER.warn("Fail to handle press {} field packet for player {}, since the player is not playing a game", tttGame.getType().getName().toLowerCase(), currentPlayer.getGameProfile().getName());
					}
				} else {
					LOGGER.warn("Player {} tries to change the {} field in map at pos {}:{} to {}, but it is not his turn", gameProfile.getName(), tttGame.getType().getName(), vMap, hMap, tttGame.getPlayerType(currentPlayer));
				}
			} else {
				LOGGER.warn("Fail to handle press {} field packet, since the current game is {}", GameTypes.TIC_TAC_TOE.getName().toLowerCase(), game.getType().getName().toLowerCase());
			}
		} else {
			LOGGER.warn("Fail to handle press {} field packet, since there is no active game", GameTypes.TIC_TAC_TOE.getName().toLowerCase());
		}
	}
	
	public void handleExitGameRequest(GameProfile gameProfile) {
		Game game = this.server.getGame();
		if (game != null) {
			game.removePlayer(this.server.getPlayerList().getPlayer(gameProfile.getUUID()));
		} else {
			for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
				if (player.isPlaying()) {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getGameProfile().getName());
				}
			}
		}
	}
	
}
