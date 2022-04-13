package net.vgc.account.network;

import java.util.UUID;

import net.vgc.account.AccountAgent;
import net.vgc.account.AccountServer;
import net.vgc.account.PlayerAccount;
import net.vgc.account.PlayerAccountInfo;
import net.vgc.common.InfoResult;
import net.vgc.common.LoginType;
import net.vgc.common.Result;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.client.ClientLoggedOutPacket;

public class AccountServerPacketListener extends AbstractPacketListener {
	
	protected final AccountServer accountServer;
	
	public AccountServerPacketListener(AccountServer accountServer, NetworkSide networkSide) {
		super(networkSide);
		this.accountServer = accountServer;
	}

	public void handleClientLogin(LoginType loginType, String name, String password) {
		this.checkSide();
		AccountAgent agent = this.accountServer.getAgent();
		PlayerAccountInfo accountInfo;
		if (loginType == LoginType.REGISTRATION) {
			accountInfo = new PlayerAccountInfo(new InfoResult(Result.SUCCESS, TranslationKey.createAndGet(Languages.EN_US, "account.login.create")), agent.createUserAccount(name, password, UUID.randomUUID()));
		} else if (loginType == LoginType.USER_LOGIN) {
			accountInfo = agent.accountLogin(name, password);
		} else if (loginType == LoginType.GUEST_LOGIN) {
			accountInfo = new PlayerAccountInfo(new InfoResult(Result.SUCCESS, TranslationKey.createAndGet(Languages.EN_US, "account.login.guest")), agent.createGuestAccount(name, UUID.randomUUID()));
		} else {
			accountInfo = new PlayerAccountInfo(new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.login.unknown")), PlayerAccount.UNKNOWN);
		}
		this.connection.send(new ClientLoggedInPacket(loginType, accountInfo));
	}
	
	public void handleClientLogout(PlayerAccount account) {
		this.checkSide();
		InfoResult infoResult = this.accountServer.getAgent().accountLogout(account.getName(), account.getPassword(), account.getUUID(), account.isGuest());
		this.connection.send(new ClientLoggedOutPacket(infoResult));
	}
	
}
