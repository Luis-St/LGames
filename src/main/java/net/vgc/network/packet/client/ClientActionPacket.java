package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.action.Action;
import net.vgc.game.action.ActionRegistry;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class ClientActionPacket implements ClientPacket {
	
	private final Action<?> action;
	
	public ClientActionPacket(Action<?> action) {
		this.action = action;
	}
	
	public ClientActionPacket(FriendlyByteBuffer buffer) {
		int id = buffer.readInt();
		this.action = ActionRegistry.getType(id).decode(buffer);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.action.id());
		this.action.data().encode(buffer);
	}
	
	@Override
	public void handle(ClientPacketHandler listener) {
		// TODO Auto-generated method stub
		
	}
	
}
