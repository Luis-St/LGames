package net.vgc.network.packet;

import net.vgc.network.Connection;

public interface PacketListener {
	
	Connection getConnection();
	
	void setConnection(Connection connection);
	
	default void onConnect() {
		
	}
	
	default void onDisconnect() {
		
	}
	
}
