package net.vgc.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.listener.PacketListener;
import net.vgc.network.packet.listener.PacketSubscriber;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber(value = NetworkSide.CLIENT, getter = "#getListener")
public class TestNonStaticListener {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@PacketListener(ClientLoggedInPacket.class)
	public void handle() {
		LOGGER.debug("1 - No");
	}
	
	@PacketListener
	public void handle(Packet<?> packet) {
		LOGGER.debug("2 - Packet");
	}
	
	@PacketListener
	public void handle(ClientLoggedInPacket packet) {
		LOGGER.debug("3 - ClientLoggedInPacket");
	}
	
	@PacketListener(ClientLoggedInPacket.class)
	public void handle(LoginType loginType, PlayerAccount account, boolean successful) {
		LOGGER.debug("4 - LoginType, PlayerAccount, boolean");
	}
	
}
