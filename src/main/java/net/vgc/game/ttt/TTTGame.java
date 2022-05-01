package net.vgc.game.ttt;

import java.util.List;
import java.util.Objects;

import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.action.ActionHandler;
import net.vgc.network.packet.client.ExitGamePacket;
import net.vgc.network.packet.client.StopGamePacket;
import net.vgc.server.Server;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;

public class TTTGame implements Game {
	
	protected final List<ServerPlayer> players;
	protected ServerPlayer currentPlayer;
	
	public TTTGame(List<ServerPlayer> players) {
		this.players = players;
	}
	
	@Override
	public GameType<TTTGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}

	@Override
	public ActionHandler getActionHandler() {
		return new TTTActionHandler(this);
	}

	@Override
	public List<ServerPlayer> getPlayers() {
		return this.players;
	}
	
	@Override
	public ServerPlayer getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public void setCurrentPlayer(ServerPlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	@Override
	public boolean removePlayer(ServerPlayer player) {
		this.players.remove(player);
		player.connection.send(new ExitGamePacket());
		if (Objects.equals(this.currentPlayer, player)) {
			this.nextPlayer();
		}
		if (!this.getType().enoughPlayersToPlay(this.players)) {
			this.stopGame();
		}
		return false;
	}
	
	@Override
	public void stopGame() {
		DedicatedServer server = Server.getInstance().getServer();
		for (ServerPlayer player : this.players) {
			player.connection.send(new StopGamePacket());
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (this.players.contains(player) && player.isPlaying()) {
				player.setPlaying(false);
			} else if (player.isPlaying()) {
				player.setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getGameProfile().getName());
			}
		}
		this.players.clear();
		server.setGame(null);
		LOGGER.info("Game {} was successfully stopped", this.getType().getName());
	}
	
}
