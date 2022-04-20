package net.vgc.network.packet;

import net.vgc.network.Connection;
import net.vgc.network.InvalidNetworkSideException;
import net.vgc.network.NetworkSide;

public interface PacketListener {
	
	Connection getConnection();
	
	void setConnection(Connection connection);
	
	NetworkSide getNetworkSide();
	
	default boolean isOn() {
		return this.getNetworkSide().isOn();
	}
	
	default void checkSide() {
		if (!this.isOn()) {
			throw new InvalidNetworkSideException(this.getNetworkSide());
		}
	}
	
}
