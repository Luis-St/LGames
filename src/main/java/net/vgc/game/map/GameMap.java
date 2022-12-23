package net.vgc.game.map;

import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;

/**
 *
 * @author Luis-st
 *
 */

public interface GameMap {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	void init();
	
	void init(List<GamePlayer> players);
	
	void addFields();
	
	Game getGame();
	
	List<GameField> getFields();
	
	default List<GameField> getFields(Predicate<GameField> predicate) {
		return this.getFields().stream().filter(predicate).collect(ImmutableList.toImmutableList());
	}
	
	@Nullable
	default GameField getField(GameFigure figure) {
		for (GameField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}
	
	@Nullable
	GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, GameFieldPos fieldPos);
	
	@Nullable
	GameField getNextField(GameFigure figure, int count);
	
	List<GameField> getHomeFields(GamePlayerType playerType);
	
	List<GameField> getStartFields(GamePlayerType playerType);
	
	List<GameField> getWinFields(GamePlayerType playerType);
	
	default boolean hasEmptyField() {
		for (GameField field : this.getFields()) {
			if (field.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	default GameFigure getFigure(GamePlayer player, int figure) {
		return player.getFigure(figure);
	}
	
	default boolean moveFigure(GameFigure figure, int count) {
		GameField field = this.getNextField(figure, count);
		if (field != null) {
			return this.moveFigureTo(figure, field);
		}
		return false;
	}
	
	boolean moveFigureTo(GameFigure figure, GameField field);
	
	@Nullable
	GameField getSelectedField();
	
	default void reset() {
		this.getFields().forEach(GameField::clear);
	}
	
}
