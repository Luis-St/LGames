package net.vgc.network.packet;

public interface PacketListener<T extends Packet<?>> {
	
	void handlePacket(T packet);
	
}
