package net.luis.server.games.ttt.player;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.Player;
import net.luis.server.game.player.AbstractServerGamePlayer;
import net.luis.server.games.ttt.player.figure.TTTServerFigure;
import net.luis.utils.util.ToString;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

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
		GamePlayer.LOGGER.warn("Fail to get roll count of player {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName());
		return -1;
	}
	
	@Override
	public final void setRollCount(int rollCount) {
		GamePlayer.LOGGER.warn("Fail to set roll count of player {} to {}, since tic tac toe is not a dice game", this.getPlayer().getProfile().getName(), rollCount);
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
