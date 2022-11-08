package net.vgc.network.packet.server;

import net.vgc.game.action.Action;
import net.vgc.game.action.ActionTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketListener;

public class ServerActionPacket implements ServerPacket {
	
	private final Action<?> action;
	
	public ServerActionPacket(Action<?> action) {
		this.action = action;
	}
	
	public ServerActionPacket(FriendlyByteBuffer buffer) {
		int id = buffer.readInt();
		this.action = ActionTypes.getType(id).decode(buffer);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.action.getType().getId());
		this.action.getData().encode(buffer);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
