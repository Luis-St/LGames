package net.vgc.network.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractPacketHandler implements PacketHandler {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private final NetworkSide networkSide;
	protected Connection connection;
	
	public AbstractPacketHandler(NetworkSide networkSide) {
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
		if (object instanceof AbstractPacketHandler packetListener) {
			return packetListener.getClass() == this.getClass();
		}
		return false;
	}
	
}
