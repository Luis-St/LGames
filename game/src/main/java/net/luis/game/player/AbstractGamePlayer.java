package net.luis.game.player;

import net.luis.application.ApplicationType;
import net.luis.game.Game;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.figure.GameFiguresFactory;

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
	
	protected AbstractGamePlayer(Game game, Player player, GamePlayerType playerType, GameFiguresFactory figuresFactory) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
		this.figures = figuresFactory.create(this);
	}
	
	@Override
	public Game getGame() {
		return this.game;
	}
	
	@Override
	public Player getPlayer() {
		return this.player;
	}
	
	@Override
	public GamePlayerType getPlayerType() {
		return this.playerType;
	}
	
	@Override
	public List<GameFigure> getFigures() {
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGamePlayer that)) return false;
		
		if (!this.player.equals(that.player)) return false;
		if (!this.playerType.equals(that.playerType)) return false;
		return this.rollCount == that.rollCount;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.player, this.playerType, this.rollCount);
	}
}
