package net.luis.game.player.game;

import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFigure;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Luis-st
 *
 */

public interface GamePlayer {
	
	@NotNull Game getGame();
	
	default @NotNull GameMap getMap() {
		return this.getGame().getMap();
	}
	
	@NotNull Player getPlayer();
	
	default @NotNull GameProfile getProfile() {
		return this.getPlayer().getProfile();
	}
	
	default @NotNull String getName() {
		return this.getPlayer().getName();
	}
	
	@NotNull GamePlayerType getPlayerType();
	
	@NotNull List<GameFigure> getFigures();
	
	default int getFigureCount() {
		return this.getFigures().size();
	}
	
	default @Nullable GameFigure getFigure(int count) {
		for (GameFigure figure : this.getFigures()) {
			if (figure.getIndex() == count) {
				return figure;
			}
		}
		LogManager.getLogger(GamePlayer.class).warn("Fail to get figure for count {} from player {}", count, this.getPlayer().getProfile().getName());
		return null;
	}
	
	default @Nullable GameFigure getFigure(@NotNull BiPredicate<GameMap, GameFigure> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (predicate.test(this.getMap(), figure)) {
				return figure;
			}
		}
		return null;
	}
	
	default boolean hasFigureAt(@NotNull Predicate<GameField> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (predicate.test(this.getMap().getField(figure))) {
				return true;
			}
		}
		return false;
	}
	
	default boolean hasAllFiguresAt(@NotNull Predicate<GameField> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (!predicate.test(this.getMap().getField(figure))) {
				return false;
			}
		}
		return true;
	}
	
	default boolean canMoveFigure(@NotNull GameFigure figure, int count) {
		return figure.canMove(this.getMap(), count);
	}
	
	default boolean canMoveAnyFigure(int count) {
		for (GameFigure figure : this.getFigures()) {
			if (this.canMoveFigure(figure, count)) {
				return true;
			}
		}
		return false;
	}
	
	@NotNull List<GameFieldPos> getWinPoses();
	
	int getRollCount();
	
	void setRollCount(int rollCount);
	
}
