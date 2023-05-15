package net.luis.game.player.game;

import net.luis.game.Game;
import net.luis.game.application.ApplicationType;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.game.player.game.figure.GameFiguresFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGamePlayer implements GamePlayer {
	
	private final Game game;
	private final Player player;
	private final GamePlayerType playerType;
	private final List<GameFigure> figures;
	private int rollCount = 0;
	
	protected AbstractGamePlayer(Game game, Player player, GamePlayerType playerType, @NotNull GameFiguresFactory figuresFactory) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = figuresFactory.create(this);
	}
	
	@Override
	public @NotNull Game getGame() {
		return this.game;
	}
	
	@Override
	public @NotNull Player getPlayer() {
		return this.player;
	}
	
	@Override
	public @NotNull GamePlayerType getPlayerType() {
		return this.playerType;
	}
	
	@Override
	public @NotNull List<GameFigure> getFigures() {
		return this.figures;
	}
	
	@Override
	public int getRollCount() {
		return ApplicationType.SERVER.isOn() ? this.rollCount : 0;
	}
	
	@Override
	public void setRollCount(int rollCount) {
		this.rollCount = ApplicationType.SERVER.isOn() ? rollCount : 0;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGamePlayer that)) return false;
		
		if (this.rollCount != that.rollCount) return false;
		if (!this.game.equals(that.game)) return false;
		if (!this.player.equals(that.player)) return false;
		if (!this.playerType.equals(that.playerType)) return false;
		return this.figures.equals(that.figures);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.game, this.player, this.playerType, this.figures, this.rollCount);
	}
	//endregion
}
