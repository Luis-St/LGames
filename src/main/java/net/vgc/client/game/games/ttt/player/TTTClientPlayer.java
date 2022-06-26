package net.vgc.client.game.games.ttt.player;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.client.game.games.ttt.player.figure.TTTClientFigure;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.player.TTTPlayerType;

public class TTTClientPlayer implements ClientGamePlayer {
	
	protected final TTTClientGame game;
	protected final AbstractClientPlayer player;
	protected final TTTPlayerType playerType;
	protected final List<TTTClientFigure> figures;
	
	public TTTClientPlayer(TTTClientGame game, AbstractClientPlayer player, TTTPlayerType playerType, List<UUID> uuids) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType, uuids);
	}
	
	protected static List<TTTClientFigure> createFigures(TTTClientPlayer player, TTTPlayerType type, List<UUID> uuids) {
		List<TTTClientFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new TTTClientFigure(player, i, uuids.get(i)));
		}
		return figures;
	}
	
	@Override
	public TTTClientGame getGame() {
		return this.game;
	}

	@Override
	public AbstractClientPlayer getPlayer() {
		return this.player;
	}

	@Override
	public TTTPlayerType getPlayerType() {
		return this.playerType;
	}

	@Override
	public List<TTTClientFigure> getFigures() {
		return this.figures;
	}

	@Override
	public TTTClientFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<TTTFieldPos> getWinPoses() {
		return Lists.newArrayList();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTClientPlayer player) {
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
		StringBuilder builder = new StringBuilder("TTTClientPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}
	
}
