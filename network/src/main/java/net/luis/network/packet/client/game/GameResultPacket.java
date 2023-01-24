package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class GameResultPacket implements ClientPacket {
	
	private final EncodableEnum result;
	private final EncodableObject object;
	
	public GameResultPacket(Enum<?> result, Encodable object) {
		this.result = new EncodableEnum(result);
		this.object = new EncodableObject(object);
	}
	
	public GameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.read(EncodableEnum.class);
		this.object = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.result);
		buffer.write(this.object);
	}
	
	@PacketGetter
	public Enum<?> getResult() {
		return this.result.get();
	}
	
	@PacketGetter
	public Encodable getObject() {
		return this.object.get();
	}
	
}
