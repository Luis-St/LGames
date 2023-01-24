package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
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
	
	private final EncodableObject result;
	private final EncodableObject object;
	
	public GameResultPacket(Encodable result, Encodable object) {
		this.result = new EncodableObject(result);
		this.object = new EncodableObject(object);
	}
	
	public GameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.read(EncodableObject.class);
		this.object = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.result);
		buffer.write(this.object);
	}
	
	@PacketGetter
	public Encodable getResult() {
		return this.result.get();
	}
	
	@PacketGetter
	public Encodable getObject() {
		return this.object.get();
	}
	
}
