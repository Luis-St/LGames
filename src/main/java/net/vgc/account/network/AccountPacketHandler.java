package net.vgc.account.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.account.AccountAgent;
import net.vgc.account.AccountServer;
import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.network.packet.account.ClientLogoutPacket;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.client.ClientLoggedOutPacket;
import net.vgc.network.packet.listener.PacketListener;
import net.vgc.network.packet.listener.PacketSubscriber;

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
	
	@PacketListener(ClientLoginPacket.class)
	public void handleClientLogin(LoginType loginType, String name, String password) {
		AccountAgent agent = this.account.getAgent();
		PlayerAccount account;
		if (loginType == LoginType.REGISTRATION) {
			account = agent.createAndLogin(name, password, false);
		} else if (loginType == LoginType.USER_LOGIN) {
			account = agent.accountLogin(name, password);
		} else if (loginType == LoginType.GUEST_LOGIN) {
			account = agent.createAndLogin(name, "", true);
		} else {
			account = PlayerAccount.UNKNOWN;
		}
		this.connection.send(new ClientLoggedInPacket(loginType, account, account == PlayerAccount.UNKNOWN ? false : true));
	}
	
	@PacketListener(ClientLogoutPacket.class)
	public void handleClientLogout(PlayerAccount account) {
		this.connection.send(new ClientLoggedOutPacket(this.account.getAgent().accountLogout(account.getName(), account.getPassword())));
	}
	
	@PacketListener(ClientExitPacket.class)
	public void handleClientLogoutExit(PlayerAccount account) {
		if (!account.getName().equals(PlayerAccount.UNKNOWN.getName()) && !account.getPassword().equals(PlayerAccount.UNKNOWN.getPassword())) {
			LOGGER.info("Logout of client with name {} and password {}", account.getName(), account.getPassword());
			this.account.getAgent().accountLogout(account.getName(), account.getPassword());
		}
		this.account.exitClient(this.connection);
	}
	
}
