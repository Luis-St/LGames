package net.vgc.client.network;

import java.util.List;

import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.client.Client;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.window.LoginWindow;
import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class ClientPacketListener extends AbstractPacketListener {
	
	protected final Client client;
	
	public ClientPacketListener(Client client, NetworkSide networkSide) {
		super(networkSide);
		this.client = client;
	}
	
	public void handleClientLoggedIn(LoginType loginType, PlayerAccount account, boolean successful) {
		LoginWindow loginWindow = this.client.getLoginWindow();
		if (!this.client.isLoggedIn()) {
			if (successful) {
				switch (loginType) {
					case REGISTRATION: {
						LOGGER.info("Create successfully a new account");
						this.client.login(account);
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} break;
					case USER_LOGIN: {
						LOGGER.debug("Successfully logged in");
						this.client.login(account);
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} break;
					case GUEST_LOGIN: {
						LOGGER.debug("Successfully logged in as a guest");
						this.client.login(account);
						if (loginWindow != null) {
							loginWindow.handleLoggedIn(loginType);
						}
					} break;
					case UNKNOWN: {
						LOGGER.warn("Fail to log in");
					} break;
				}
			} else {
				LOGGER.warn("Fail to log in");
			}
		} else {
			LOGGER.warn("Fail to log in, since already logged in");
		}
	}
	
	public void handleClientLoggedOut(boolean successful) {
		LoginWindow loginWindow = this.client.getLoginWindow();
		if (successful) {
			LOGGER.info("Successfully logged out");
			this.client.logout();
			if (loginWindow != null) {
				loginWindow.handleLoggedOut();
			}
		} else {
			LOGGER.warn("Fail to log out");
		}
	}
	
	public void handleClientJoined(List<GameProfile> profiles) {
		for (GameProfile profile : profiles) {
			if (this.client.getAccount().getUUID().equals(profile.getUUID())) {
				this.client.setPlayer(new LocalPlayer(profile));
			} else {
				this.client.addRemotePlayer(new RemotePlayer(profile));
			}
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	public void handlePlayerAdd(GameProfile profile) {;
		if (this.client.getAccount().getUUID().equals(profile.getUUID())) {
			if (this.client.getPlayer() == null) {
				LOGGER.warn("The local player is not set, that was not supposed to be");
				this.client.setPlayer(new LocalPlayer(profile));
			} else {
				LOGGER.warn("The local player is already set to {}, but there is another player with the same id {}", this.client.getPlayer().getProfile(), profile);
			}
		} else {
			this.client.addRemotePlayer(new RemotePlayer(profile));
		}
	}
	
	public void handlePlayerRemove(GameProfile profile) {
		if (this.client.getAccount().getUUID().equals(profile.getUUID())) {
			this.client.removePlayer();
		} else {
			this.client.removeRemotePlayer(new RemotePlayer(profile));
		}
	}
	
	public void handleSyncPermission(GameProfile profile) {
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			if (player.getProfile().equals(profile)) {
				player.setAdmin(true);
				LOGGER.debug("Player {} is now a admin", player.getProfile().getName());
			} else {
				player.setAdmin(false);
			}
		}
		LOGGER.info("Sync admins");
	}
	
	public void handleSyncPlayerData(GameProfile profile, boolean playing, PlayerScore score) {
		AbstractClientPlayer player = this.client.getPlayer(profile);
		if (player != null) {
			player.setPlaying(playing);
			player.getScore().sync(score);
			LOGGER.info("Synchronize data from server of player {}", profile.getName());
		} else {
			LOGGER.warn("Fail to synchronize data from server to player {}, since the player does not exists", profile.getName());
		}
	}
	
	public void handleCancelRollDiceRequest() {
		LOGGER.info("Roll dice request was canceled from the server");
		this.client.getPlayer().setCanRollDice(false);
	}
	
	public void handleRolledDice(int count) {
		LocalPlayer player = this.client.getPlayer();
		if (Mth.isInBounds(count, 1, 6)) {
			player.setCount(count);
			player.setCanRollDice(false);
		} else {
			player.setCount(-1);
			player.setCanRollDice(false);
		}
	}
	
	public void handleCanRollDiceAgain() {
		this.client.getPlayer().setCanRollDice(true);
	}
	
	public void handleCurrentPlayerUpdate(GameProfile profile) {
		boolean flag = false;
		if (this.client.getGame() != null) {
			Game game = this.client.getGame();
			for (AbstractClientPlayer player : this.client.getPlayers()) {
				if (player.getProfile().equals(profile)) {
					game.setPlayer(game.getPlayerFor(player));
					player.setCurrent(true);
					flag = true;
					if (player instanceof LocalPlayer localPlayer) {
						localPlayer.setCanRollDice(true);
					}
				} else {
					player.setCurrent(false);
				}
			}
			if (!flag) {
				LOGGER.warn("Fail to update the current player to {}, since the player does not exists", profile.getName());
			}
		} else {
			LOGGER.warn("Fail to update the current player to {}, since there is no active game", profile.getName());
		}
	}
	
	public <S extends Game, C extends Game> void handleStartGame(GameType<S, C> gameType, List<GamePlayerInfo> playerInfos) {
		if (this.client.getGame() == null) {
			C game = gameType.createClientGame(this.client, playerInfos);
			game.startGame();
			boolean flag = false;
			for (Player player : Util.mapList(game.getPlayers(), GamePlayer::getPlayer)) {
				player.setPlaying(true);
				if (this.client.getPlayer().getProfile().equals(player.getProfile())) {
					flag = true;
				}
			}
			if (flag) {
				gameType.openScreen(this.client, game);
				LOGGER.info("Start game {}", gameType.getInfoName());
				this.client.setGame(game);
			} else {
				LOGGER.warn("Fail to start game {}, since the local player is not in the player list of the game", gameType.getInfoName());
				this.client.setScreen(new LobbyScreen());
			}
		}
	}
	
	public void handleCanSelectGameField() {
		LocalPlayer player = this.client.getPlayer();
		if (player.isCurrent()) {
			player.setCanSelect(true);
		}
	}
	
	public void handleGameActionFailed() {
		this.client.getPlayer().setCurrent(true);
	}
	
	public void handleCancelPlayAgainGameRequest() {
		LOGGER.warn("The request to play again was canceled by the server");
	}
	
	public void handleExitGame() {
		LOGGER.info("Exit the current game");
		if (this.client.getPlayer().isPlaying()) {
			this.client.getPlayer().setPlaying(false);
			this.client.getPlayer().getScore().reset();
			if (this.client.getGame() != null) {
				this.client.setGame(null);
			} else {
				LOGGER.warn("Received a exit game packet, but there is no active game");
			}
		} else {
			LOGGER.info("Received a exit game packet, but the local player is not playing a game");
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	public void handleStopGame() {;
		LOGGER.info("Stopping the current game");
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			player.setPlaying(false);
			player.getScore().reset();
		}
		if (this.client.getGame() != null) {
			this.client.setGame(null);
		} else {
			LOGGER.warn("Received a stop game packet, but there is no active game");
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	public void handleServerClosed() {
		this.client.getServerHandler().close();
		this.client.removePlayer();
		this.client.setScreen(new MenuScreen());
	}
	
}
