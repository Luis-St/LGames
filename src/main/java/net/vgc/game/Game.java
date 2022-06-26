package net.vgc.game;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.client.game.ClientGame;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.dice.DiceHandler;

public interface Game {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	void initGame();
	
	void startGame();
	
	GameType<? extends ServerGame, ? extends ClientGame> getType();
	
	GameMap getMap();
	
	default String getName(Player player) {
		return player.getProfile().getName();
	}
	
	default String getName(GamePlayer player) {
		return this.getName(player.getPlayer());
	}
	
	List<? extends GamePlayer> getPlayers();
	
	List<? extends GamePlayer> getEnemies(GamePlayer player);
	
	@Nullable
	GamePlayer getPlayerFor(Player player);
	
	@Nullable
	GamePlayer getPlayerFor(GameProfile profile);
	
	@Nullable
	default GamePlayerType getPlayerType(GamePlayer player) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.equals(player)) {
				return gamePlayer.getPlayerType();
			}
		}
		return null;
	}
	
	@Nullable
	GamePlayer getCurrentPlayer();
	
	void setCurrentPlayer(GamePlayer player);
	
	GamePlayer getStartPlayer();
	
	default void nextPlayer(boolean random) {
		List<? extends GamePlayer> players = this.getPlayers();
		if (!players.isEmpty()) {
			if (random) {
				this.setCurrentPlayer(players.get(new Random().nextInt(players.size())));
			} else {
				GamePlayer player = this.getCurrentPlayer();
				if (player == null) {
					this.setCurrentPlayer(players.get(0));
				} else {
					int index = players.indexOf(player);
					if (index != -1) {
						index++;
						if (index >= players.size()) {
							this.setCurrentPlayer(players.get(0));
						} else {
							this.setCurrentPlayer(players.get(index));
						}
					} else {
						LOGGER.warn("Fail to get next player, since the player {} does not exists", this.getName(player));
						this.setCurrentPlayer(players.get(0));
					}
				}
			}
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
		}
	}
	
	boolean removePlayer(GamePlayer player, boolean sendExit);
	
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
