package net.luis.game.player.game.figure;

import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFigure {
	
	@NotNull GamePlayer getPlayer();
	
	default @NotNull GamePlayerType getPlayerType() {
		return this.getPlayer().getPlayerType();
	}
	
	int getIndex();
	
	@NotNull UUID getUniqueId();
	
	@NotNull GameFieldPos getHomePos();
	
	@NotNull GameFieldPos getStartPos();
	
	default boolean canMove(GameMap map, int count) {
		return this.canMove(map, map.getField(this), map.getNextField(this, count));
	}
	
	default boolean canMove(GameMap map, @Nullable GameField currentField, @Nullable GameField nextField) {
		if (nextField != null) {
			return nextField.isEmpty() || (Objects.requireNonNull(nextField.getFigure()).isKickable() && this.canKick(nextField.getFigure()));
		}
		return false;
	}
	
	default boolean isKickable() {
		return false;
	}
	
	default boolean canKick(GameFigure figure) {
		return false;
	}
}
