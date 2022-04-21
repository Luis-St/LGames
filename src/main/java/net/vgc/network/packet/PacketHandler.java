package net.vgc.network.packet;

public interface PacketHandler<T extends Packet<?>> {
	
	void handlePacket(T packet);
	
}
