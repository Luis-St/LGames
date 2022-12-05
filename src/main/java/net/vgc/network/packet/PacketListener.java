package net.vgc.network.packet;

/**
 *
 * @author Luis-st
 *
 */

public interface PacketListener<T extends Packet<?>> {
	
	void handlePacket(T packet);
	
}
