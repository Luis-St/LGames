package net.vgc.server.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.collect.Lists;

import net.vgc.game.Game;
import net.vgc.game.action.GameAction;
import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.specific.StartGameData;
import net.vgc.game.action.handler.GameActionHandler;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.type.GameType;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketHandler;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.action.GlobalServerActionHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private final DedicatedServer server;
	private final GlobalServerActionHandler actionHandler;
	
	public ServerPacketHandler(DedicatedServer server, NetworkSide networkSide) {
		super(networkSide);
		this.server = server;
		this.actionHandler = new GlobalServerActionHandler(this.server);
	}
	
	@Override
	public void setConnection(Connection connection) {
		super.setConnection(connection);
		this.actionHandler.setConnection(this.connection);
	}
	
	public void handleClientJoin(String name, UUID uuid) {
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(this.server.getPlayerList().getPlayers()));
	}
	
	public void handleClientLeave(UUID uuid) {
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
	public void handleAction(GameAction<?> action) {
		GameActionHandler specificHandler = null;
		Game game = this.server.getGame();
		if (game != null) {
			if (!Objects.requireNonNull(game.getActionHandler(), "The action handler of a game must not be null").handle(action)) {
				LOGGER.warn("Fail to handle a action");
			}
		}
		if (!action.handleType().handle(action, specificHandler, this.actionHandler)) {
			LOGGER.warn("Fail to handle a action of type {}", action.type().getName());
		}
	}
	
	public <S extends Game, C extends Game> void handlePlayGameRequest(GameType<S, C> gameType, List<GameProfile> profiles) {
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
						game.start();
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							GameActionTypes.START_GAME.send(player.connection, new StartGameData(gameType, this.createPlayerInfos(game.getPlayers())));
						}
						Util.runDelayed("DelayedSetStartPlayer", 250, () -> {
							game.getStartPlayer();
						});
					}
				} else {
					LOGGER.warn("Fail to start game {}, since there is already a running game {}", gameType.getInfoName(), this.server.getGame().getType().getInfoName());
					GameActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
				}
			} else {
				LOGGER.warn("Fail to start game {}, since on player is already playing a game (this should normally not happen)", gameType.getName());
				GameActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
			}
		} else {
			if (mutable.isTrue()) {
				LOGGER.warn("Fail to start game {}, since at least one selected player is already playing a game", gameType.getInfoName());
			} else {
				LOGGER.warn("Fail to start game {}, since there was an error in a player profile", gameType.getInfoName());
			}
			GameActionTypes.CANCEL_PLAY_REQUEST.send(this.connection, new EmptyData());
		}
	}
	
	private List<GamePlayerInfo> createPlayerInfos(List<GamePlayer> players) {
		List<GamePlayerInfo> playerInfos = Lists.newArrayList();
		for (GamePlayer player : players) {
			playerInfos.add(new GamePlayerInfo(player.getPlayer().getProfile(), player.getPlayerType(), Util.mapList(player.getFigures(), GameFigure::getUUID)));
		}
		return playerInfos;
	}
	
}
