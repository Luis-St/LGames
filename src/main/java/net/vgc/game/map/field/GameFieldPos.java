package net.vgc.game.map.field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.ReflectionHelper;

public interface GameFieldPos extends Encodable {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int getPosition();
	
	int getPositionFor(GamePlayerType playerType);
	
	boolean isStart();
	
	@SuppressWarnings("unchecked")
	public static GameFieldPos decode(FriendlyByteBuffer buffer) {
		String className = buffer.readString();
		try {
			Class<? extends GameFieldPos> clazz = (Class<? extends GameFieldPos>) Class.forName(className);
			if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
				return ReflectionHelper.newInstance(clazz, buffer);
			}
		} catch (Exception e) {
			LOGGER.warn("Fail to decode game field pos");
		}
		return null;
	}
	
}
