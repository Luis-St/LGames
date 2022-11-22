package net.vgc.account.network;

import net.vgc.account.AccountAgent;
import net.vgc.account.AccountServer;
import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketHandler;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.client.ClientLoggedOutPacket;

public class AccountServerPacketHandler extends AbstractPacketHandler {
	
	private final AccountServer accountServer;
	
	public AccountServerPacketHandler(AccountServer accountServer, NetworkSide networkSide) {
		super(networkSide);
		this.accountServer = accountServer;
	}
	
	public void handleClientLogin(LoginType loginType, String name, String password) {
		AccountAgent agent = this.accountServer.getAgent();
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
	
	public void handleClientLogout(String name, String password) {
		this.connection.send(new ClientLoggedOutPacket(this.accountServer.getAgent().accountLogout(name, password)));
	}
	
	public void handleClientLogoutExit(String name, String password) {
		if (!name.equals(PlayerAccount.UNKNOWN.getName()) && !password.equals(PlayerAccount.UNKNOWN.getPassword())) {
			LOGGER.info("Logout of client with name {} and password {}", name, password);
			this.accountServer.getAgent().accountLogout(name, password);
		}
		this.accountServer.exitClient(this.connection);
	}
	
}
