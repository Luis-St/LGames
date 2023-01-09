package net.luis.server.games.wins4.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.games.wins4.map.field.Wins4FieldPos;
import net.luis.utils.util.ToString;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerFigure extends AbstractGameFigure {
	
	public Wins4ServerFigure(GamePlayer player, int count, UUID uuid) {
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
	
	@Override
	public String toString() {
		return ToString.toString(this, "player");
	}
	
}
