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
import java.util.Objects;
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
	
	default GameFigure getFigure(int count) {
		return this.getFigures().stream().filter(figure -> figure.getIndex() == count).findFirst().orElseGet(() -> {
			LogManager.getLogger(GamePlayer.class).warn("Fail to get figure for count {} from player {}", count, this.getPlayer().getProfile().getName());
			return null;
		});
	}
	
	default GameFigure getFigure(BiPredicate<GameMap, GameFigure> predicate) {
		return this.getFigures().stream().filter(figure -> predicate.test(this.getMap(), figure)).findFirst().orElse(null);
	}
	
	default boolean hasFigureAt(Predicate<GameField> predicate) {
		return this.getFigures().stream().anyMatch(figure -> predicate.test(this.getMap().getField(figure)));
	}
	
	default boolean hasAllFiguresAt(Predicate<GameField> predicate) {
		return this.getFigures().stream().allMatch(figure -> predicate.test(this.getMap().getField(figure)));
	}
	
	default boolean canMoveFigure(GameFigure figure, int count) {
		return Objects.requireNonNull(figure, "Game figure must not be null").canMove(this.getMap(), count);
	}
	
	default boolean canMoveAnyFigure(int count) {
		return this.getFigures().stream().anyMatch(figure -> this.canMoveFigure(figure, count));
	}
	
	@NotNull List<GameFieldPos> getWinPoses();
	
	int getRollCount();
	
	void setRollCount(int rollCount);
}
