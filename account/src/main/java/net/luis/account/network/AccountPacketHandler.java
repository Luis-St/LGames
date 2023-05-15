package net.luis.account.network;

import net.luis.account.AccountServer;
import net.luis.account.account.Account;
import net.luis.account.account.AccountType;
import net.luis.account.account.LoginType;
import net.luis.network.Connection;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.PacketHandler;
import net.luis.network.packet.account.ClientExitPacket;
import net.luis.network.packet.account.ClientLoginPacket;
import net.luis.network.packet.account.ClientLogoutPacket;
import net.luis.network.packet.account.ClientRegistrationPacket;
import net.luis.network.packet.client.ClientLoggedInPacket;
import net.luis.network.packet.client.ClientLoggedOutPacket;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class AccountPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(AccountPacketHandler.class);
	
	private final AccountServer account;
	private Connection connection;
	
	public AccountPacketHandler(@NotNull AccountServer account) {
		this.account = account;
	}
	
	@PacketListener(ClientRegistrationPacket.class)
	public void handleClientRegistration(@NotNull String name, @NotNull String mail, int passwordHash, @NotNull String firstName, @NotNull String lastName, @NotNull Date birthday) {
		Account account = this.account.getAccountAgent().createAndLogin(name, mail, passwordHash, firstName, lastName, birthday, AccountType.USER);
		this.connection.send(new ClientLoggedInPacket(LoginType.REGISTRATION, account.getName(), account.getId(), account.getMail(), account.getUUID()));
	}
	
	@PacketListener(ClientLoginPacket.class)
	public void handleClientLogin(@NotNull LoginType loginType, @NotNull String name, int passwordHash) {
		Account account = switch (loginType) {
			case USER_LOGIN -> this.account.getAccountAgent().accountLogin(name, passwordHash);
			case GUEST_LOGIN -> this.account.getAccountAgent().createAccount(name, "", new Random().nextInt(), name, "Guest", new Date(), AccountType.GUEST);
			default -> Account.UNKNOWN;
		};
		this.connection.send(new ClientLoggedInPacket(account == Account.UNKNOWN ? LoginType.UNKNOWN : loginType, account.getName(), account.getId(), account.getMail(), account.getUUID()));
	}
	
	@PacketListener(ClientLogoutPacket.class)
	public void handleClientLogout(@NotNull String name, int id, @NotNull UUID uuid) {
		this.connection.send(new ClientLoggedOutPacket(this.account.getAccountAgent().accountLogout(name, id, uuid)));
	}
	
	@PacketListener(ClientExitPacket.class)
	public void handleClientLogoutExit(@NotNull String name, int id, @NotNull UUID uuid) {
		if (!name.isEmpty() && id != -1 && !uuid.equals(Utils.EMPTY_UUID)) {
			this.account.getAccountAgent().accountLogout(name, id, uuid);
		}
		String address = this.connection.getChannel().remoteAddress().toString().replace("/", "");
		LOGGER.info("Client disconnected with address {}", address);
		this.connection.close();
	}
}
