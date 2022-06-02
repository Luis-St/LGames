package net.vgc.server.game.games.ludo.player;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.exception.InvalidValueException;

public class LudoServerPlayer implements ServerGamePlayer, PacketHandler<ServerPacket> {
	
	protected final LudoServerGame game;
	protected final ServerPlayer player;
	protected final LudoPlayerType playerType;
	protected final List<LudoServerFigure> figures;
	protected int rollCount = 0;
	
	public LudoServerPlayer(LudoServerGame game, ServerPlayer player, LudoPlayerType playerType, int figureCount) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType, figureCount);
	}
	
	protected static List<LudoServerFigure> createFigures(LudoServerPlayer player, LudoPlayerType type, int figureCount) {
		List<LudoServerFigure> figures = Lists.newArrayList();
		if (!Mth.isInBounds(figureCount, 1, 4)) {
			LOGGER.error("Fail to create figure list for count {}, since the count is out of bound 1 - 4", figureCount);
			throw new InvalidValueException("Fail to create figure list for count " + figureCount + "{}, since the count is out of bound 1 - 4");
		}
		for (int i = 0; i < figureCount; i++) {
			figures.add(new LudoServerFigure(player, i));
		}
		return figures;
	}
	
	@Override
	public ServerGame getGame() {
		return this.game;
	}
	
	@Override
	public ServerPlayer getPlayer() {
		return this.player;
	}

	@Override
	public LudoPlayerType getPlayerType() {
		return this.playerType;
	}

	@Override
	public List<LudoServerFigure> getFigures() {
		return this.figures;
	}

	@Override
	public LudoServerFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<LudoFieldPos> getWinPoses() {
		return Lists.newArrayList(LudoFieldPos.of(0), LudoFieldPos.of(1), LudoFieldPos.of(2), LudoFieldPos.of(3));
	}
	
	@Override
	public int getRollCount() {
		return this.rollCount;
	}
	
	@Override
	public void setRollCount(int rollCount) {
		this.rollCount = Math.max(0, rollCount);
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGamePlayer.super.handlePacket(serverPacket);
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoServerPlayer player) {
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
		StringBuilder builder = new StringBuilder("LudoServerPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}

}
