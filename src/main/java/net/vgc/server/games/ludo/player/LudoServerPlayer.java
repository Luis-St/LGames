package net.vgc.server.games.ludo.player;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.vgc.game.Game;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldPos;
import net.vgc.player.Player;
import net.vgc.server.game.player.AbstractServerGamePlayer;
import net.vgc.server.games.ludo.player.figure.LudoServerFigure;
import net.vgc.util.Mth;
import net.vgc.util.ToString;
import net.vgc.util.exception.InvalidValueException;

public class LudoServerPlayer extends AbstractServerGamePlayer {
	
	private final List<GameFigure> figures;
	
	public LudoServerPlayer(Game game, Player player, GamePlayerType playerType, int figureCount) {
		super(game, player, playerType);
		this.figures = createFigures(this, playerType, figureCount);
	}
	
	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, int figureCount) {
		List<GameFigure> figures = Lists.newArrayList();
		if (!Mth.isInBounds(figureCount, 1, 4)) {
			LOGGER.error("Fail to create figure list for count {}, since the count is out of bound 1 - 4", figureCount);
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
