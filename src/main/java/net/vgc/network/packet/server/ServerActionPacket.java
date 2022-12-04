package net.vgc.network.packet.server;

import net.vgc.game.action.GameAction;
import net.vgc.game.action.ActionRegistry;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketHandler;

public class ServerActionPacket implements ServerPacket {
	
	private final GameAction<?> action;
	
	public ServerActionPacket(GameAction<?> action) {
		this.action = action;
	}
	
	public ServerActionPacket(FriendlyByteBuffer buffer) {
		int id = buffer.readInt();
		this.action = ActionRegistry.getType(id).decode(buffer);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.action.id());
		this.action.data().encode(buffer);
	}
	
	@Override
	public void handle(ServerPacketHandler listener) {
		listener.handleAction(this.action);
	}
	
}
