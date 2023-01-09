package net.luis.account.network;

import net.luis.account.AccountServer;
import net.luis.account.account.Account;
import net.luis.account.account.AccountType;
import net.luis.account.account.LoginType;
import net.luis.utils.util.Utils;
import net.luis.network.Connection;
import net.luis.network.NetworkSide;
import net.luis.network.packet.PacketHandler;
import net.luis.network.packet.account.ClientExitPacket;
import net.luis.network.packet.account.ClientLoginPacket;
import net.luis.network.packet.account.ClientLogoutPacket;
import net.luis.network.packet.account.ClientRegistrationPacket;
import net.luis.network.packet.client.ClientLoggedInPacket;
import net.luis.network.packet.client.ClientLoggedOutPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
			case GUEST_LOGIN -> this.account.getManager().createAccount(name, "", new Random().nextInt(), name, "Guest", new Date(), AccountType.GUEST);
			default -> Account.UNKNOWN;
		};
		this.connection.send(new ClientLoggedInPacket(account == Account.UNKNOWN ? LoginType.UNKNOWN : loginType, account.getName(), account.getId(), account.getMail(), account.getUUID()));
	}
	
	@PacketListener(ClientLogoutPacket.class)
	public void handleClientLogout(String name, int id, UUID uuid) {
		this.connection.send(new ClientLoggedOutPacket(this.account.getManager().accountLogout(name, id, uuid)));
	}
	
	@PacketListener(ClientExitPacket.class)
	public void handleClientLogoutExit(String name, int id, UUID uuid) {
		if (!name.isEmpty() && id != -1 && !uuid.equals(Utils.EMPTY_UUID)) {
			this.account.getManager().accountLogout(name, id, uuid);
			LOGGER.info("Logout of account {}#{}", name, id);
		}
		this.account.exitClient(this.connection);
	}
	
}
