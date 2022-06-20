package net.vgc.game.map.field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Encodable;

public interface GameFieldPos extends Encodable {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int getPosition();
	
	int getPositionFor(GamePlayerType playerType);
	
	boolean isStart();
	
	boolean isOutOfMap();
	
}
