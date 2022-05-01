package net.vgc.server.network;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;

import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.CancelPlayGameRequestPacket;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.network.packet.client.StartGamePacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;

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
						for (ServerPlayer player : players) {
							player.setPlaying(true);
							player.connection.send(new StartGamePacket(gameType, players));
						}
					}
				} else {
					LOGGER.warn("Fail to start game {}, since there is already a running game {}", gameType.getName(), this.server.getGame().getType().getName());
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
				LOGGER.warn("Fail to start game {}, since there was an error in a GameProfile", gameType.getName());
			}
			this.connection.send(new CancelPlayGameRequestPacket());
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
