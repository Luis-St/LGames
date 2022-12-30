package net.vgc.account.network;

import net.vgc.account.AccountServer;
import net.vgc.account.account.Account;
import net.vgc.account.account.AccountType;
import net.vgc.account.account.LoginType;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.network.packet.account.ClientLogoutPacket;
import net.vgc.network.packet.account.ClientRegistrationPacket;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.client.ClientLoggedOutPacket;
import net.vgc.network.packet.listener.PacketListener;
import net.vgc.network.packet.listener.PacketSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber(value = NetworkSide.ACCOUNT, getter = "#getPacketHandler")
public class AccountPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final AccountServer account;
	private Connection connection;
	
	public AccountPacketHandler(AccountServer account) {
		this.account = account;
	}
	
	@PacketListener(ClientRegistrationPacket.class)
	public void handleClientRegistration(String name, String mail, int passwordHash, String firstName, String lastName, Date birthday) {
		Account account = this.account.getManager().createAndLogin(name, mail, passwordHash, firstName, lastName, birthday, AccountType.USER);
		this.connection.send(new ClientLoggedInPacket(LoginType.REGISTRATION, account.getName(), account.getId(), account.getMail(), account.getUUID()));
	}
	
	@PacketListener(ClientLoginPacket.class)
	public void handleClientLogin(LoginType loginType, String name, int passwordHash) {
		Account account = switch (loginType) {
			case USER_LOGIN -> this.account.getManager().accountLogin(name, passwordHash);
			case GUEST_LOGIN -> this.account.getManager().createAccount(name, "guest@vgc.net", new Random().nextInt(), name, "Guest", new Date(), AccountType.GUEST);
			default -> Account.UNKNOWN;
		};
		this.connection.send(new ClientLoggedInPacket(loginType, account.getName(), account.getId(), account.getMail(), account.getUUID()));
	}
	
	@PacketListener(ClientLogoutPacket.class)
	public void handleClientLogout(String name, int id, int passwordHash) {
		this.connection.send(new ClientLoggedOutPacket(this.account.getManager().accountLogout(name, id, passwordHash)));
	}
	
	@PacketListener(ClientExitPacket.class)
	public void handleClientLogoutExit(String name, int id, int passwordHash) {
		this.account.getManager().accountLogout(name, id, passwordHash);
		LOGGER.info("Logout of account {}#{}", name, id);
		this.account.exitClient(this.connection);
	}
	
}
