package net.vgc.game.map;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;

public interface GameMap {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	void init(List<? extends GamePlayer> players);
	
	Game getGame();
	
	List<? extends GameField> getFields();
	
	@Nullable
	GameField getField(GameFigure figure);
	
	@Nullable
	GameField getField(GameFieldType fieldType, @Nullable GamePlayerType playerType, GameFieldPos fieldPos);
	
	@Nullable
	GameField getNextField(GameFigure figure, int count);
	
	List<? extends GameField> getHomeFields(GamePlayerType playerType);
	
	List<? extends GameField> getStartFields(GamePlayerType playerType);
	
	List<? extends GameField> getWinFields(GamePlayerType playerType);
	
	default boolean hasEmptyField() {
		for (GameField field : this.getFields()) {
			if (field.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	GameFigure getFigure(GamePlayer player, int figure);
	
	default boolean moveFigure(GameFigure figure, int count) {
		GameField field = this.getNextField(figure, count);
		if (field != null) {
			return this.moveFigureTo(figure, field);
		}
		return false;
	}
	
	boolean moveFigureTo(GameFigure figure, GameField field);
	
	default void reset() {
		this.getFields().forEach(GameField::clear);
	}
	
}
