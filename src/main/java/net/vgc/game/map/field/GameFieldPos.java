package net.vgc.game.map.field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Encodable;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFieldPos extends Encodable {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int getPosition();
	
	default int getRow() {
		LOGGER.warn("By default a field pos has no rows");
		return -1;
	}
	
	default int getColumn() {
		LOGGER.warn("By default a field pos has no columns");
		return -1;
	}
	
	int getPositionFor(GamePlayerType playerType);
	
	boolean isStart();
	
	boolean isOutOfMap();
	
}
