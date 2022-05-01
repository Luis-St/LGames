package net.vgc.game.ttt;

import java.util.List;

import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.action.ActionHandler;
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
	
}
