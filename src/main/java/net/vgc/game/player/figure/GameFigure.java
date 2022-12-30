package net.vgc.game.player.figure;

import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFigure {
	
	Logger LOGGER = LogManager.getLogger();
	
	GamePlayer getPlayer();
	
	default GamePlayerType getPlayerType() {
		return this.getPlayer().getPlayerType();
	}
	
	int getCount();
	
	UUID getUUID();
	
	GameFieldPos getHomePos();
	
	GameFieldPos getStartPos();
	
	default boolean canMove(GameMap map, int count) {
		return this.canMove(map, map.getField(this), map.getNextField(this, count));
	}
	
	default boolean canMove(GameMap map, GameField currentField, GameField nextField) {
		if (nextField != null) {
			return nextField.isEmpty() || (nextField.getFigure().isKickable() && this.canKick(nextField.getFigure()));
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
