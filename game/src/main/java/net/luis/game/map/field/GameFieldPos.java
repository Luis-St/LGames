package net.luis.game.map.field;

import net.luis.game.player.game.GamePlayerType;
import net.luis.network.buffer.Encodable;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFieldPos extends Encodable {
	
	int getPosition();
	
	default int getRow() {
		LogManager.getLogger(GameFieldPos.class).warn("By default a field pos has no rows");
		return -1;
	}
	
	default int getColumn() {
		LogManager.getLogger(GameFieldPos.class).warn("By default a field pos has no columns");
		return -1;
	}
	
	int getPositionFor(@NotNull GamePlayerType playerType);
	
	boolean isStart();
	
	boolean isOutOfMap();
	
}
