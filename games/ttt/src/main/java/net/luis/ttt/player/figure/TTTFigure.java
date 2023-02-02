package net.luis.ttt.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.ttt.map.field.TTTFieldPos;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class TTTFigure extends AbstractGameFigure {
	
	
	public TTTFigure(GamePlayer player, int count, UUID uuid) {
		super(player, count, uuid);
	}
	
	@Override
	public GameFieldPos getHomePos() {
		return TTTFieldPos.NO;
	}
	
	@Override
	public GameFieldPos getStartPos() {
		return TTTFieldPos.NO;
	}
}
