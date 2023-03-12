package net.luis.ludo.player;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.AbstractGamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFiguresFactory;
import net.luis.ludo.map.field.LudoFieldPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class LudoPlayer extends AbstractGamePlayer {
	
	public LudoPlayer(Game game, Player player, GamePlayerType playerType, GameFiguresFactory figuresFactory) {
		super(game, player, playerType, figuresFactory);
	}
	
	// TODO: add figure creation
/*	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, List<UUID> uuids) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new LudoClientFigure(player, i, uuids.get(i)));
		}
		return figures;
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
	}*/
	
	@Override
	public @NotNull List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList(LudoFieldPos.of(0), LudoFieldPos.of(1), LudoFieldPos.of(2), LudoFieldPos.of(3));
	}
	
}
