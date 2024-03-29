package net.vgc.server.games.ludo.player.figure;

import net.luis.utils.util.ToString;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.AbstractGameFigure;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldPos;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerFigure extends AbstractGameFigure {
	
	public LudoServerFigure(GamePlayer player, int count, UUID uuid) {
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
