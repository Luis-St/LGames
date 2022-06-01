package net.vgc.game.map.field;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.player.field.GameFigure;

public interface GameField {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	GameFieldType getFieldType();
	
	GameFieldPos getFieldPos();
	
	boolean isHome();
	
	boolean isStart();
	
	boolean isStartFor(GameFigure figure);
	
	boolean isWin();
	
	default boolean isSpecial() {
		return this.isHome() || this.isStart() || this.isWin();
	}
	
	@Nullable
	GameFigure getFigure();
	
	void setFigure(@Nullable GameFigure figure);
	
	default void clear() {
		this.setFigure(null);
	}
	
	default boolean isEmpty() {
		return this.getFigure() == null;
	}
	
}
