package net.vgc.network.packet.server;

import java.util.Objects;

import net.vgc.game.action.GameEvent;
import net.vgc.game.action.GameEvents;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketListener;

public class GameEventPacket implements ServerPacket {
	
	protected final GameEvent event;
	
	public GameEventPacket(GameEvent event) {
		this.event = event;
	}
	
	public GameEventPacket(FriendlyByteBuffer buffer) {
		int id = buffer.readInt();
		this.event = Objects.requireNonNull(GameEvents.getEvent(id, buffer), "Fail to decode game event with id " + id + " from FriendlyByteBuffer");
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(GameEvents.getId(this.event.getClass()));
		this.event.encode(buffer);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleGameEvent(this.event);
	}

}
