package net.luis.game.player;

import net.luis.game.Game;
import net.luis.player.GameProfile;

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
