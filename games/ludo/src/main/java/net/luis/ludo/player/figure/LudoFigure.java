package net.luis.ludo.player.figure;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.figure.AbstractGameFigure;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ludo.map.field.LudoFieldPos;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class LudoFigure extends AbstractGameFigure {
	
	public LudoFigure(GamePlayer player, int count, UUID uniqueId) {
		super(player, count, uniqueId);
	}
	
	@Override
	public @NotNull GameFieldPos getHomePos() {
		return LudoFieldPos.of(this.getIndex());
	}
	
	@Override
	public @NotNull GameFieldPos getStartPos() {
		return LudoFieldPos.of(this.getPlayerType(), 0);
	}
	
	@Override
	public boolean isKickable() {
		return true;
	}
	
	@Override
	public boolean canKick(@NotNull GameFigure figure) {
		return !this.equals(figure) && this.getPlayerType() != figure.getPlayerType();
	}
	
}
