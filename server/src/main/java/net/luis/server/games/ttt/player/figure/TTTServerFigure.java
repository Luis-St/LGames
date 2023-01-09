package net.luis.server.games.ttt.player.figure;

import net.luis.utils.util.ToString;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.games.ttt.map.field.TTTFieldPos;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class TTTServerFigure extends AbstractGameFigure {
	
	public TTTServerFigure(GamePlayer player, int count, UUID uuid) {
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
	
	@Override
	public String toString() {
		return ToString.toString(this, "player");
	}
	
}
