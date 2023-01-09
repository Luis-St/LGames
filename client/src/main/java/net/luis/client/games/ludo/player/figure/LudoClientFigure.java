package net.luis.client.games.ludo.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.AbstractGameFigure;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.ludo.map.field.LudoFieldPos;
import net.luis.utils.util.ToString;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class LudoClientFigure extends AbstractGameFigure {
	
	public LudoClientFigure(GamePlayer player, int count, UUID uuid) {
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
	
	@Override
	public String toString() {
		return ToString.toString(this, "player");
	}
	
}
