package net.luis.wins4.player;

import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.AbstractGamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.Player;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.figure.GameFiguresFactory;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4Player extends AbstractGamePlayer {
	
	public Wins4Player(Game game, Player player, GamePlayerType playerType, GameFiguresFactory figuresFactory) {
		super(game, player, playerType, figuresFactory);
	}
	
	// TODO: add figure creation
/*	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, List<UUID> uuids) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uuids.size(); i++) {
			figures.add(new Wins4Figure(player, i, uuids.get(i)));
		}
		return figures;
	}*/
	
	@Override
	public List<GameFieldPos> getWinPoses() {
		return null;
	}
	
	public GameFigure getUnplacedFigure() {
		for (GameFigure figure : this.getFigures()) {
			if (this.getMap().getField(figure) == null) {
				return figure;
			}
		}
		return null;
	}
	
}
