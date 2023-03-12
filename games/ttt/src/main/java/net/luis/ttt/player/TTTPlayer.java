package net.luis.ttt.player;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.AbstractGamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFiguresFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class TTTPlayer extends AbstractGamePlayer {
	
	public TTTPlayer(Game game, Player player, GamePlayerType playerType, GameFiguresFactory figuresFactory) {
		super(game, player, playerType, figuresFactory);
	}
	
	// TODO: add figure creation
/*	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, List<UUID> uuids) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new TTTFigure(player, i, uuids.get(i)));
		}
		return figures;
	}*/
	
	@Override
	public @NotNull List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList();
	}
}
