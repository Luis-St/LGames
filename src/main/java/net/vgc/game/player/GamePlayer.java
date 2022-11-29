package net.vgc.game.player;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.vgc.game.Game;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.player.Player;

public interface GamePlayer {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	Player getPlayer();
	
	default String getName() {
		return this.getPlayer().getName();
	}
	
	GamePlayerType getPlayerType();
	
	default GameMap getMap() {
		return this.getGame().getMap();
	}
	
	List<GameFigure> getFigures();
	
	default int getFigureCount() {
		return this.getFigures().size();
	}
	
	@Nullable
	default GameFigure getFigure(int count) {
		for (GameFigure figure : this.getFigures()) {
			if (figure.getCount() == count) {
				return figure;
			}
		}
		LOGGER.warn("Fail to get figure for count {} from player {}", count, this.getPlayer().getProfile().getName());
		return null;
	}
	
	@Nullable
	default GameFigure getFigure(BiPredicate<GameMap, GameFigure> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (predicate.test(this.getMap(), figure)) {
				return figure;
			}
		}
		return null;
	}
	
	default boolean hasFigureAt(Predicate<GameField> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (predicate.test(this.getMap().getField(figure))) {
				return true;
			}
		}
		return false;
	}
	
	default boolean hasAllFiguresAt(Predicate<GameField> predicate) {
		for (GameFigure figure : this.getFigures()) {
			if (!predicate.test(this.getMap().getField(figure))) {
				return false;
			}
		}
		return true;
	}
	
	default boolean canMoveFigure(GameFigure figure, int count) {
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
	
	List<GameFieldPos> getWinPoses();
	
	int getRollCount();
	
	void setRollCount(int rollCount);
	
}
