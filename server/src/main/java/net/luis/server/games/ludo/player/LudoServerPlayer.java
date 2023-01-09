package net.luis.server.games.ludo.player;

import com.google.common.collect.Lists;
import net.luis.exception.InvalidValueException;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.ludo.map.field.LudoFieldPos;
import net.luis.player.Player;
import net.luis.server.game.player.AbstractServerGamePlayer;
import net.luis.server.games.ludo.player.figure.LudoServerFigure;
import net.luis.utils.math.Mth;
import net.luis.utils.util.ToString;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerPlayer extends AbstractServerGamePlayer {
	
	private final List<GameFigure> figures;
	
	public LudoServerPlayer(Game game, Player player, GamePlayerType playerType, int figureCount) {
		super(game, player, playerType);
		this.figures = createFigures(this, playerType, figureCount);
	}
	
	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, int figureCount) {
		List<GameFigure> figures = Lists.newArrayList();
		if (!Mth.isInBounds(figureCount, 1, 4)) {
			GamePlayer.LOGGER.error("Fail to create figure list for count {}, since the count is out of bound 1 - 4", figureCount);
			throw new InvalidValueException("Fail to create figure list for count " + figureCount + "{}, since the count is out of bound 1 - 4");
		}
		for (int i = 0; i < figureCount; i++) {
			figures.add(new LudoServerFigure(player, i, UUID.randomUUID()));
		}
		return figures;
	}
	
	@Override
	public List<GameFigure> getFigures() {
		return this.figures;
	}
	
	@Override
	public List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList(LudoFieldPos.of(0), LudoFieldPos.of(1), LudoFieldPos.of(2), LudoFieldPos.of(3));
	}
	
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else if (object instanceof LudoServerPlayer player) {
			return this.figures.equals(player.figures);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
