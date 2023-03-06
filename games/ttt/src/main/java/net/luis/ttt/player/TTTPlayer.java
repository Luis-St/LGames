package net.luis.ttt.player;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.AbstractGamePlayer;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.Player;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.figure.GameFiguresFactory;
import net.luis.ttt.player.figure.TTTFigure;

import java.util.List;
import java.util.UUID;

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
	public List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList();
	}
}