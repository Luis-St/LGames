package net.luis.client.network;

import net.luis.account.account.LoginType;
import net.luis.client.Client;
import net.luis.client.player.AbstractClientPlayer;
import net.luis.client.player.LocalPlayer;
import net.luis.client.player.RemotePlayer;
import net.luis.client.screen.LobbyScreen;
import net.luis.client.screen.MenuScreen;
import net.luis.client.window.LoginWindow;
import net.luis.game.Game;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.network.packet.PacketHandler;
import net.luis.network.packet.client.*;
import net.luis.network.packet.client.game.*;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.luis.network.packet.client.game.dice.RolledDicePacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.player.GameProfile;
import net.luis.player.Player;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getPacketHandler")
public class ClientPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Client client;
	
	public ClientPacketHandler(Client client) {
		this.client = client;
	}
	
	@PacketListener(ClientLoggedInPacket.class)
	public void handleClientLoggedIn(LoginType loginType, String name, int id, String mail, UUID uuid) {
		LoginWindow loginWindow = this.client.getLoginWindow();
		if (!this.client.isLoggedIn()) {
			switch (loginType) {
				case REGISTRATION -> {
					LOGGER.info("Create successfully a new account");
					this.client.login(name, id, mail, uuid, false);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
				}
				case USER_LOGIN -> {
					LOGGER.debug("Successfully logged in");
					this.client.login(name, id, mail, uuid, false);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
				}
				case GUEST_LOGIN -> {
					LOGGER.debug("Successfully logged in as a guest");
					this.client.login(name, id, mail, uuid, true);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
					this.client.setPassword("");
				}
				case UNKNOWN -> {
					LOGGER.warn("Fail to log in");
					this.client.setPassword("");
				}
			}
		} else {
			LOGGER.warn("Fail to log in, since already logged in");
		}
	}
	
	@PacketListener(ClientLoggedOutPacket.class)
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
	
	@PacketListener(ClientJoinedPacket.class)
	public void handleClientJoined(List<GameProfile> profiles) {
		for (GameProfile profile : profiles) {
			if (this.client.getAccount().uuid().equals(profile.getUUID())) {
				this.client.setPlayer(new LocalPlayer(profile));
			} else {
				this.client.addRemotePlayer(new RemotePlayer(profile));
			}
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@PacketListener(PlayerAddPacket.class)
	public void handlePlayerAdd(GameProfile profile) {
		if (this.client.getAccount().uuid().equals(profile.getUUID())) {
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
	
	@PacketListener(PlayerRemovePacket.class)
	public void handlePlayerRemove(GameProfile profile) {
		if (this.client.getAccount().uuid().equals(profile.getUUID())) {
			this.client.removePlayer();
		} else {
			this.client.removeRemotePlayer(new RemotePlayer(profile));
		}
	}
	
	@PacketListener(SyncPermissionPacket.class)
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
	
	@PacketListener(SyncPlayerDataPacket.class)
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
	
	@PacketListener(CancelRollDiceRequestPacket.class)
	public void handleCancelRollDiceRequest() {
		LOGGER.info("Roll dice request was canceled from the server");
		this.client.getPlayer().setCanRollDice(false);
	}
	
	@PacketListener(RolledDicePacket.class)
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
	
	@PacketListener(CanRollDiceAgainPacket.class)
	public void handleCanRollDiceAgain() {
		this.client.getPlayer().setCanRollDice(true);
	}
	
	@PacketListener(CurrentPlayerUpdatePacket.class)
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
	
	@PacketListener(StartGamePacket.class)
	public <S extends Game, C extends Game> void handleStartGame(GameType<S, C> gameType, List<GamePlayerInfo> playerInfos) {
		if (this.client.getGame() == null) {
			C game = gameType.createClientGame(this.client, playerInfos);
			game.start();
			boolean flag = false;
			for (Player player : Utils.mapList(game.getPlayers(), GamePlayer::getPlayer)) {
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
	
	@PacketListener(CanSelectGameFieldPacket.class)
	public void handleCanSelectGameField() {
		LocalPlayer player = this.client.getPlayer();
		if (player.isCurrent()) {
			player.setCanSelect(true);
		}
	}
	
	@PacketListener(GameActionFailedPacket.class)
	public void handleGameActionFailed() {
		this.client.getPlayer().setCurrent(true);
	}
	
	@PacketListener(CancelPlayAgainGameRequestPacket.class)
	public void handleCancelPlayAgainGameRequest() {
		LOGGER.warn("The request to play again was canceled by the server");
	}
	
	@PacketListener(ExitGamePacket.class)
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
	
	@PacketListener(StopGamePacket.class)
	public void handleStopGame() {
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
	
	@PacketListener(ServerClosedPacket.class)
	public void handleServerClosed() {
		this.client.getServerHandler().close();
		this.client.removePlayer();
		this.client.setScreen(new MenuScreen());
	}
	
}
