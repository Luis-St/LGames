package net.luis.game.player;

import net.luis.game.Game;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GamePlayerFactory {
	
	GamePlayer create(Game game, GameProfile profile, GamePlayerType playerType, List<UUID> uuids);
	
}
