package net.vgc.client.network;

import java.util.List;

import net.vgc.account.PlayerAccountInfo;
import net.vgc.client.Client;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.window.LoginWindow;
import net.vgc.common.InfoResult;
import net.vgc.common.LoginType;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.player.GameProfile;

public class ClientPacketListener extends AbstractPacketListener {
	
	protected final Client client;
	
	public ClientPacketListener(Client client, NetworkSide networkSide) {
		super(networkSide);
		this.client = client;
	}
	
	public void handleClientLoggedIn(LoginType loginType, PlayerAccountInfo accountInfo) {
		this.checkSide();
		LoginWindow loginWindow = this.client.getLoginWindow();
		InfoResult infoResult = accountInfo.infoResult();
		if (!this.client.isLoggedIn()) {
			switch (loginType) {
				case REGISTRATION: {
					if (accountInfo.isSuccess()) {
						LOGGER.info("Create successfully a new account");
						this.client.login(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
					}
				} break;
				case USER_LOGIN: {
					if (accountInfo.isSuccess()) {
						LOGGER.debug("Successfully logged in");
						this.client.login(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
					}
				} break;
				case GUEST_LOGIN: {
					if (accountInfo.isSuccess()) {
						LOGGER.debug("Successfully logged in as a guest");
						this.client.login(accountInfo.account());
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} else {
						LOGGER.warn("Fail to log in: {}", infoResult.info());
						if (loginWindow != null) {
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
	
	public void handleClientLoggedOut(InfoResult infoResult) {
		this.checkSide();
		LoginWindow loginWindow = this.client.getLoginWindow();
		if (infoResult.isSuccess()) {
			LOGGER.info("Successfully logged out");
			this.client.logout();
			if (loginWindow != null) {
				loginWindow.handleLoggedOut();
			}
		} else {
			LOGGER.warn("Fail to log out: {}", infoResult.info());
		}
	}
	
	public void handleClientJoined(List<GameProfile> gameProfiles) {
		for (GameProfile gameProfile : gameProfiles) {
			if (this.client.getAccount().getUUID().equals(gameProfile.getUUID())) {
				this.client.setPlayer(new LocalPlayer(gameProfile));
			} else {
				this.client.addRemotePlayer(new RemotePlayer(gameProfile));
			}
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	public void handleClientPlayerAdd(GameProfile gameProfile) {
		if (this.client.getAccount().getUUID().equals(gameProfile.getUUID())) {
			if (this.client.getPlayer() == null) {
				LOGGER.warn("The local player is not set, that was not supposed to be");
				this.client.setPlayer(new LocalPlayer(gameProfile));
			} else {
				LOGGER.warn("The local player is already set to {}, but there is another player with the same id {}", this.client.getPlayer().getGameProfile(), gameProfile);
			}
		} else {
			this.client.addRemotePlayer(new RemotePlayer(gameProfile));
		}
	}
	
	public void handleClientPlayerRemove(GameProfile gameProfile) {
		if (this.client.getAccount().getUUID().equals(gameProfile.getUUID())) {
			this.client.removePlayer();
		} else {
			this.client.removeRemotePlayer(new RemotePlayer(gameProfile));
		}
	}
	
	public void handleServerClosed() {
		this.client.getServerConnection().close();
		this.client.removePlayer();
		this.client.setScreen(new MenuScreen());
	}
	
}
