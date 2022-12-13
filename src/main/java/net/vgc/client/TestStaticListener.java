package net.vgc.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.common.application.GameApplication;
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

@PacketSubscriber(NetworkSide.CLIENT)
public class TestStaticListener {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@PacketListener
	public static void handle() {
		LOGGER.debug("1 - No");
	}
	
	@PacketListener
	public static void handle(GameApplication application) {
		LOGGER.debug("2 - GameApplication");
	}
	
	@PacketListener
	public static void handle(Client client) {
		LOGGER.debug("3 - Client");
	}
	
	@PacketListener
	public static void handle(GameApplication application, Packet<?> packet) {
		LOGGER.debug("4 - GameApplication, Packet");
	}
	
	@PacketListener
	public static void handle(Client appliclientcation, Packet<?> packet) {
		LOGGER.debug("5 - Client, Packet");
	}
	
	@PacketListener
	public static void handle(GameApplication application, ClientLoggedInPacket packet) {
		LOGGER.debug("6 - GameApplication, ClientLoggedInPacket");
	}
	
	@PacketListener
	public static void handle(Client client, ClientLoggedInPacket packet) {
		LOGGER.debug("7 - Client, ClientLoggedInPacket");
	}
	
	@PacketListener(ClientLoggedInPacket.class)
	public static void handle(GameApplication application, LoginType loginType, PlayerAccount account, boolean successful) {
		LOGGER.debug("8 - GameApplication, LoginType, PlayerAccount, boolean");
	}
	
	@PacketListener(ClientLoggedInPacket.class)
	public static void handle(Client client, LoginType loginType, PlayerAccount account, boolean successful) {
		LOGGER.debug("9 - Client, LoginType, PlayerAccount, boolean");
	}
	
}
