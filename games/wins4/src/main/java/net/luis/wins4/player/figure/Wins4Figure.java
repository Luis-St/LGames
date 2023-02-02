package net.luis.wins4.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.wins4.map.field.Wins4FieldPos;

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
	public GameFieldPos getHomePos() {
		return Wins4FieldPos.NO;
	}
	
	@Override
	public GameFieldPos getStartPos() {
		return Wins4FieldPos.NO;
	}
}
