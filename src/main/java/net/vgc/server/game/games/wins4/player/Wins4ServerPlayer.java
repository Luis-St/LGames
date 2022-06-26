package net.vgc.server.game.games.wins4.player;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.server.game.games.wins4.Wins4ServerGame;
import net.vgc.server.game.games.wins4.player.figure.Wins4ServerFigure;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.player.ServerPlayer;

public class Wins4ServerPlayer implements ServerGamePlayer {
	
	protected final Wins4ServerGame game;
	protected final ServerPlayer player;
	protected final Wins4PlayerType playerType;
	protected final List<Wins4ServerFigure> figures;
	
	public Wins4ServerPlayer(Wins4ServerGame game, ServerPlayer player, Wins4PlayerType playerType) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = createFigures(this, this.playerType);
	}
	
	protected static List<Wins4ServerFigure> createFigures(Wins4ServerPlayer player, Wins4PlayerType type) {
		List<Wins4ServerFigure> figures = Lists.newArrayList();
		for (int i = 0; i < 21; i++) {
			figures.add(new Wins4ServerFigure(player, i));
		}
		return figures;
	}
	
	@Override
	public Wins4ServerGame getGame() {
		return this.game;
	}

	@Override
	public ServerPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Wins4PlayerType getPlayerType() {
		return this.playerType;
	}

	@Override
	public List<Wins4ServerFigure> getFigures() {
		return this.figures;
	}

	@Override
	public Wins4ServerFigure getFigure(int figure) {
		return this.figures.get(figure);
	}

	@Override
	public List<Wins4FieldPos> getWinPoses() {
		return Lists.newArrayList();
	}

	@Override
	public int getRollCount() {
		LOGGER.warn("Fail to get roll count of player {}, since the 4 wins is not a dice game", this.getPlayer().getProfile().getName());
		return -1;
	}

	@Override
	public void setRollCount(int rollCount) {
		LOGGER.warn("Fail to set roll count of player {} to {}, since the 4 wins is not a dice game", this.getPlayer().getProfile().getName(), rollCount);
	}
	
	@Nullable
	public Wins4ServerFigure getUnplacedFigure() {
		for (Wins4ServerFigure figure : this.figures) {
			if (this.getMap().getField(figure) == null) {
				return figure;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ServerPlayer player) {
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
		StringBuilder builder = new StringBuilder("Win4ServerPlayer{");
		builder.append("game=").append(this.game).append(",");
		builder.append("player=").append(this.player).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("figures=").append(this.figures).append("}");
		return builder.toString();
	}

}
