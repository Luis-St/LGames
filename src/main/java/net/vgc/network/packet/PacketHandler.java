package net.vgc.network.packet;

import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;

/**
 *
 * @author Luis-st
 *
 */

public interface PacketHandler {
	
	Connection getConnection();
	
	void setConnection(Connection connection);
	
	NetworkSide getNetworkSide();
	
}
