package net.luis.network.packet.server;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.utils.util.Utils;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class PlayGameRequestPacket implements ServerPacket {
	
	private final int gameType;
	private final List<EncodableObject> profiles;
	
	public PlayGameRequestPacket(int gameType, List<Encodable> profiles) {
		this.gameType = gameType;
		this.profiles = Utils.mapList(profiles, EncodableObject::new);
	}
	
	public PlayGameRequestPacket(FriendlyByteBuffer buffer) {
		this.gameType = buffer.readInt();
		this.profiles = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.gameType);
		buffer.writeList(this.profiles, buffer::write);
	}
	
	@PacketGetter
	public int getGameType() {
		return this.gameType;
	}
	
	@PacketGetter
	public List<Encodable> getProfiles() {
		return Utils.mapList(this.profiles, EncodableObject::get);
	}
	
}
