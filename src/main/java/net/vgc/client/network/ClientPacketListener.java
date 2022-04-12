package net.vgc.client.network;

import net.vgc.client.Client;
import net.vgc.client.window.LoginWindow;
import net.vgc.common.InfoResult;
import net.vgc.common.LoginType;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.server.account.PlayerAccountInfo;

public class ClientPacketListener extends AbstractPacketListener {
	
	protected final Client client;
	
	public ClientPacketListener(Client client, NetworkSide networkSide) {
		super(networkSide);
		this.client = client;
	}
	
	public void handleClientLoggedIn(LoginType loginType, PlayerAccountInfo accountInfo) {
		this.checkSide();
		LoginWindow loginWindow = LoginWindow.getInstance();
		InfoResult infoResult = accountInfo.infoResult();
		if (!this.client.isLoggedIn()) {
			switch (loginType) {
				case REGISTRATION: {
					if (accountInfo.isSuccess()) {
						LOGGER.info("Create successfully a new account");
						this.client.setAccount(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
						}
					}
				} break;
				case USER_LOGIN: {
					if (accountInfo.isSuccess()) {
						LOGGER.debug("Successfully logged in");
						this.client.setAccount(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
						}
					}
				} break;
				case GUEST_LOGIN: {
					if (accountInfo.isSuccess()) {
						LOGGER.debug("Successfully logged in as a guest");
						this.client.setAccount(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
						if (loginWindow != null) {
							loginWindow.setInfo(loginType, infoResult.result(), infoResult.info());
							loginWindow.handleLoggedIn(loginType);
						}
					}
				} break;
				case UNKNOWN: {
					if (accountInfo.isSuccess()) {
						LOGGER.warn("Fail to log in");
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
					}
				} break;
			}
		} else {
			LOGGER.warn("Fail to log in, since already logged in");
		}
	}
	
	public void handleClientLoggedOut(InfoResult infoResult) { // TODO: window message & add button
		this.checkSide();
		if (infoResult.isSuccess()) {
			LOGGER.info("Successfully logged out");
			this.client.setAccount(null);
		} else {
			LOGGER.warn("Fail to log out: {}", infoResult.info());
		}
	}
	
}
