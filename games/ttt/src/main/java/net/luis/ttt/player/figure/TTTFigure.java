package net.luis.ttt.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.figure.AbstractGameFigure;
import net.luis.ttt.map.field.TTTFieldPos;
import org.jetbrains.annotations.NotNull;

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
	public @NotNull GameFieldPos getHomePos() {
		return TTTFieldPos.NO;
	}
	
	@Override
	public @NotNull GameFieldPos getStartPos() {
		return TTTFieldPos.NO;
	}
}
