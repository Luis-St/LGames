package net.luis.wins4.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.figure.AbstractGameFigure;
import net.luis.wins4.map.field.Wins4FieldPos;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4Figure extends AbstractGameFigure {
	
	public Wins4Figure(GamePlayer player, int count, UUID uuid) {
		super(player, count, uuid);
	}
	
	@Override
	public @NotNull GameFieldPos getHomePos() {
		return Wins4FieldPos.NO;
	}
	
	@Override
	public @NotNull GameFieldPos getStartPos() {
		return Wins4FieldPos.NO;
	}
}
