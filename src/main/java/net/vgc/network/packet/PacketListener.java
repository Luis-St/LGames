package net.vgc.network.packet;

/**
 *
 * @author Luis-st
 *
 */

public interface PacketListener<T extends Packet<?>> {
	
	@Deprecated // Rename
	void handlePacket(T packet);
	
}
