package net.vgc.game;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.dice.DiceHandler;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.score.GameScore;

public interface Game {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	void initGame();
	
	void startGame();
	
	GameType<? extends Game> getType();
	
	List<GamePlayer> getPlayers();
	
	GameScore getScore();
	
	GameMap getMap();
	
	@Nullable
	GamePlayer getCurrentPlayer();
	
	void setCurrentPlayer(GamePlayer player);
	
	GamePlayer getStartPlayer();
	
	void nextPlayer(boolean random);
	
	boolean removePlayer(GamePlayer player);
	
	@Nullable
	GamePlayerType getPlayerType(GamePlayer player);
	
	default boolean isDiceGame() {
		return false;
	}
	
	@Nullable
	default DiceHandler getDiceHandler() {
		return null;
	}
	
	boolean nextMatch();
	
	void stopGame();
	
}
