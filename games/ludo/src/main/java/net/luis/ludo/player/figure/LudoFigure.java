package net.luis.ludo.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.game.player.figure.GameFigure;
import net.luis.ludo.map.field.LudoFieldPos;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class LudoFigure extends AbstractGameFigure {
	
	public LudoFigure(GamePlayer player, int count, UUID uuid) {
		super(player, count, uuid);
	}
	
	@Override
	public GameFieldPos getHomePos() {
		return LudoFieldPos.of(this.getCount());
	}
	
	@Override
	public GameFieldPos getStartPos() {
		return LudoFieldPos.of(this.getPlayerType(), 0);
	}
	
	@Override
	public boolean isKickable() {
		return true;
	}
	
	@Override
	public boolean canKick(GameFigure figure) {
		return !this.equals(figure) && this.getPlayerType() != figure.getPlayerType();
	}
	
}
