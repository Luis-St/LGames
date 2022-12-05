package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.action.ActionRegistry;
import net.vgc.game.action.GameAction;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class ClientActionPacket implements ClientPacket {
	
	private final GameAction<?> action;
	
	public ClientActionPacket(GameAction<?> action) {
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
		listener.handleAction(this.action);
	}
	
}
