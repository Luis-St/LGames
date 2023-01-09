package net.luis.game.map.field;

import net.luis.game.player.GamePlayerType;
import net.luis.network.buffer.Encodable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFieldPos extends Encodable {
	
	Logger LOGGER = LogManager.getLogger();
	
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
