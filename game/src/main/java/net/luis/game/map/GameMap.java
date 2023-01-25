package net.luis.game.map;

import com.google.common.collect.ImmutableList;
import net.luis.game.Game;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author Luis-st
 *
 */

public interface GameMap {
	
	Logger LOGGER = LogManager.getLogger();
	
	default void init() {
	
	}
	
	default void init(List<GamePlayer> players) {
		if (ApplicationType.CLIENT.isOn()) {
			this.getFields().forEach(GameField::clear);
		}
	}
	
	void addFields();
	
	Game getGame();
	
	List<GameField> getFields();
	
	default List<GameField> getFields(Predicate<GameField> predicate) {
		return this.getFields().stream().filter(predicate).collect(ImmutableList.toImmutableList());
	}
	
	@Nullable
	default GameField getField(GameFigure figure) {
		for (GameField field : this.getFields()) {
			if (!field.isEmpty() && Objects.equals(field.getFigure(), figure)) {
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
	
	default boolean moveFigureTo(GameFigure figure, GameField field) {
		return ApplicationType.SERVER.isOn();
	}
	
	@Nullable
	default GameField getSelectedField() {
		return null;
	}
	
	default void reset() {
		this.getFields().forEach(GameField::clear);
	}
	
}
