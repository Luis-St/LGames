package net.vgc.game.map.field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Decodable;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;

public interface GameFieldPos extends Encodable, Decodable {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int getPosition();
	
	int getPositionFor(GamePlayerType playerType);
	
	boolean isStart();
	
	@Override
	default void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.getDecoderId());
	}
	
	public static GameFieldPos decode(FriendlyByteBuffer buffer) {
		int id = buffer.readInt();
		return (GameFieldPos) Decodable.getDecoder(id).apply(buffer);
	}
	
}
