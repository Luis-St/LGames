package net.luis.game.map;

import com.google.common.collect.ImmutableList;
import net.luis.game.Game;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import org.jetbrains.annotations.NotNull;
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
	
	default void init() {
	
	}
	
	default void init(List<GamePlayer> players) {
		if (ApplicationType.CLIENT.isOn()) {
			this.getFields().forEach(GameField::clear);
		}
	}
	
	void addFields();
	
	@NotNull Game getGame();
	
	@NotNull List<GameField> getFields();
	
	default @NotNull List<GameField> getFields(Predicate<GameField> predicate) {
		return this.getFields().stream().filter(predicate).collect(ImmutableList.toImmutableList());
	}
	
	default GameField getField(GameFigure figure) {
		return this.getFields().stream().filter(field -> !field.isEmpty() && Objects.equals(field.getFigure(), figure)).findFirst().orElse(null);
	}
	
	GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, GameFieldPos fieldPos);
	
	GameField getNextField(GameFigure figure, int count);
	
	@NotNull List<GameField> getHomeFields(GamePlayerType playerType);
	
	@NotNull List<GameField> getStartFields(GamePlayerType playerType);
	
	@NotNull List<GameField> getWinFields(GamePlayerType playerType);
	
	default boolean hasEmptyField() {
		return this.getFields().stream().anyMatch(GameField::isEmpty);
	}
	
	default GameFigure getFigure(GamePlayer player, int figure) {
		return Objects.requireNonNull(player, "Game player must not be null").getFigure(figure);
	}
	
	default boolean moveFigure(GameFigure figure, int count) {
		GameField field = this.getNextField(figure, count);
		if (field != null) {
			return this.moveFigureTo(figure, field);
		}
		return false;
	}
	
	default boolean moveFigureTo(GameFigure figure, GameField field) {
		return false;
	}
	
	default GameField getSelectedField() {
		return null;
	}
	
	default void reset() {
		this.getFields().forEach(GameField::clear);
	}
}
