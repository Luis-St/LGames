package net.vgc.server.game.games.ttt.player;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.games.ttt.TTTServerGame;
import net.vgc.server.game.games.ttt.player.figure.TTTServerFigure;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.player.ServerPlayer;

public class TTTServerPlayer implements ServerGamePlayer {
	
	protected final TTTServerGame game;
	protected final ServerPlayer player;
	protected final TTTPlayerType playerType;
	protected final List<TTTServerFigure> figures;
	
	public TTTServerPlayer(TTTServerGame game, ServerPlayer player, TTTPlayerType playerType) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType);
	}
	
	protected static List<TTTServerFigure> createFigures(TTTServerPlayer player, TTTPlayerType type) {
		List<TTTServerFigure> figures = Lists.newArrayList();
		for (int i = 0; i < 5; i++) {
			figures.add(new TTTServerFigure(player, i));
		}
		return figures;
	}
	
	@Override
	public TTTServerGame getGame() {
		return this.game;
	}

	@Override
	public ServerPlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public TTTPlayerType getPlayerType() {
		return this.playerType;
	}
	
	@Override
	public List<TTTServerFigure> getFigures() {
		return this.figures;
	}

	@Override
	public TTTServerFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<TTTFieldPos> getWinPoses() {
		return Lists.newArrayList(TTTFieldPos.NO);
	}

	@Override
	public int getRollCount() {
		LOGGER.warn("Fail to get roll count of player {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName());
		return -1;
	}

	@Override
	public void setRollCount(int rollCount) {
		LOGGER.warn("Fail to set roll count of player {} to {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName(), rollCount);
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGamePlayer.super.handlePacket(serverPacket);
		
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTServerPlayer player) {
			if (!this.game.equals(player.game)) {
				return false;
			} else if (!this.player.equals(player.player)) {
				return false;
			} else if (!this.playerType.equals(player.playerType)) {
				return false;
			} else {
				return this.figures.equals(player.figures);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTServerPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}
	
}
