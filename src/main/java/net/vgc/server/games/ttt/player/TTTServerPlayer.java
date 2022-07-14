package net.vgc.server.games.ttt.player;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.game.Game;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.player.Player;
import net.vgc.server.game.player.AbstractServerGamePlayer;
import net.vgc.server.games.ttt.player.figure.TTTServerFigure;
import net.vgc.util.ToString;

public class TTTServerPlayer extends AbstractServerGamePlayer {
	
	private final List<GameFigure> figures;
	
	public TTTServerPlayer(Game game, Player player, GamePlayerType playerType) {
		super(game, player, playerType);
		this.figures = createFigures(this, playerType);
	}
	
	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < 5; i++) {
			figures.add(new TTTServerFigure(player, i, UUID.randomUUID()));
		}
		return figures;
	}
	
	@Override
	public List<GameFigure> getFigures() {
		return this.figures;
	}

	@Override
	public List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList();
	}

	@Override
	public final int getRollCount() {
		LOGGER.warn("Fail to get roll count of player {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName());
		return -1;
	}

	@Override
	public final void setRollCount(int rollCount) {
		LOGGER.warn("Fail to set roll count of player {} to {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName(), rollCount);
	}
	
	@Nullable
	public GameFigure getUnplacedFigure() {
		for (GameFigure figure : this.figures) {
			if (this.getMap().getField(figure) == null) {
				return figure;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else if (object instanceof TTTServerPlayer player) {
			return this.figures.equals(player.figures);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
