package net.luis.game;

import net.luis.game.map.GameMap;
import net.luis.game.player.GamePlayer;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGame implements Game {
	
	private final GameMap map;
	private final List<GamePlayer> players;
	private GamePlayer player;
	
	protected AbstractGame(Function<Game, GameMap> mapFunction, Function<Game, List<GamePlayer>> playersFunction) {
		this.map = mapFunction.apply(this);
		this.players = playersFunction.apply(this);
	}
	
	@Override
	public void init() {
		this.map.init(this.players);
	}
	
	@Override
	public GameMap getMap() {
		return this.map;
	}
	
	@Override
	public List<GamePlayer> getPlayers() {
		return this.players;
	}
	
	@Override
	public GamePlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public void setPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Utils.runIfNotNull(this.getPlayer(), GamePlayer::getName), Utils.runIfNotNull(player, GamePlayer::getName));
		this.player = player;
	}
	
}
