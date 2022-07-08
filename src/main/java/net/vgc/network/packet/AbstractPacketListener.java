package net.vgc.network.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;

public abstract class AbstractPacketListener implements PacketListener {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private final NetworkSide networkSide;
	protected Connection connection;
	
	public AbstractPacketListener(NetworkSide networkSide) {
		this.networkSide = networkSide;
	}
	
	@Override
	public NetworkSide getNetworkSide() {
		return this.networkSide;
	}
	
	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof AbstractPacketListener packetListener) {
			return packetListener.getClass() == this.getClass();
		}
		return false;
	}
	
}
