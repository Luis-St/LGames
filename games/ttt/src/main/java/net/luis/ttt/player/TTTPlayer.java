package net.luis.ttt.player;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.Player;
import net.luis.game.player.game.AbstractGamePlayer;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ttt.player.figure.TTTFigure;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class TTTPlayer extends AbstractGamePlayer {
	
	public TTTPlayer(Game game, Player player, GamePlayerType playerType, List<UUID> uniqueIds) {
		super(game, player, playerType, gamePlayer -> createFigures(gamePlayer, playerType, uniqueIds));
	}
	
	private static List<GameFigure> createFigures(GamePlayer player, GamePlayerType type, List<UUID> uniqueIds) {
		List<GameFigure> figures = Lists.newArrayList();
		for (int i = 0; i < uniqueIds.size(); i++) {
			figures.add(new TTTFigure(player, i, uniqueIds.get(i)));
		}
		return figures;
	}
	
	@Override
	public @NotNull List<GameFieldPos> getWinPoses() {
		return Lists.newArrayList();
	}
}
