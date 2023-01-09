package net.luis.client.games.wins4.player.figure;

import net.luis.utils.util.ToString;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.games.wins4.map.field.Wins4FieldPos;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ClientFigure extends AbstractGameFigure {
	
	public Wins4ClientFigure(GamePlayer player, int count, UUID uuid) {
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
