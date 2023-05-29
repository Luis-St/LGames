package net.luis.game.map.field;

import net.luis.game.player.game.GamePlayerType;
import net.luis.netcore.buffer.decode.Decodable;
import net.luis.netcore.buffer.encode.Encodable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface GameFieldPos extends Encodable, Decodable {
	
	int getPosition();
	
	default int getRow() {
		LogManager.getLogger(GameFieldPos.class).warn("By default a field pos has no rows");
		return -1;
	}
	
	default int getColumn() {
		LogManager.getLogger(GameFieldPos.class).warn("By default a field pos has no columns");
		return -1;
	}
	
	int getPositionFor(GamePlayerType playerType);
	
	boolean isStart();
	
	boolean isOutOfMap();
}
