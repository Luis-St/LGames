package net.luis.client.network;

import net.luis.account.account.LoginType;
import net.luis.client.Client;
import net.luis.client.account.AccountManager;
import net.luis.client.player.LocalPlayer;
import net.luis.client.players.ClientPlayerList;
import net.luis.client.screen.LobbyScreen;
import net.luis.client.screen.MenuScreen;
import net.luis.client.window.LoginWindow;
import net.luis.game.Game;
import net.luis.game.GameManager;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.game.player.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.network.Connection;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.PacketHandler;
import net.luis.network.packet.client.*;
import net.luis.network.packet.client.game.*;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.luis.network.packet.client.game.dice.RolledDicePacket;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientPacketHandler implements PacketHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(ClientPacketHandler.class);
	
	private final Client client;
	private final AccountManager accountManager;
	private Connection connection;
	
	public ClientPacketHandler(@NotNull Client client) {
		this.client = client;
		this.accountManager = this.client.getAccountManager();
	}
	
	@PacketListener(ClientLoggedInPacket.class)
	public void handleClientLoggedIn(@NotNull LoginType loginType, @NotNull String name, int id, @NotNull String mail, @NotNull UUID uuid) {
		AccountManager accountManager = this.client.getAccountManager();
		LoginWindow loginWindow = accountManager.getLoginWindow();
		if (!accountManager.isLoggedIn()) {
			switch (loginType) {
				case REGISTRATION -> {
					LOGGER.info("Create successfully a new account");
					accountManager.login(name, id, mail, uuid, false);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
				}
				case USER_LOGIN -> {
					LOGGER.debug("Successfully logged in");
					accountManager.login(name, id, mail, uuid, false);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
				}
				case GUEST_LOGIN -> {
					LOGGER.debug("Successfully logged in as a guest");
					accountManager.login(name, id, mail, uuid, true);
					if (loginWindow != null) {
						loginWindow.handleLoggedIn();
					}
					accountManager.setPassword("");
				}
				case UNKNOWN -> {
					LOGGER.warn("Fail to log in");
					accountManager.setPassword("");
				}
			}
		} else {
			LOGGER.warn("Fail to log in, since already logged in");
		}
	}
	
	@PacketListener(ClientLoggedOutPacket.class)
	public void handleClientLoggedOut(boolean successful) {
		AccountManager accountManager = this.client.getAccountManager();
		LoginWindow loginWindow = accountManager.getLoginWindow();
		if (successful) {
			LOGGER.info("Successfully logged out");
			accountManager.logout();
			if (loginWindow != null) {
				loginWindow.handleLoggedOut();
			}
		} else {
			LOGGER.warn("Fail to log out");
		}
	}
	
	@PacketListener(ClientJoinedPacket.class)
	public void handleClientJoined(@NotNull List<GameProfile> profiles) {
		ClientPlayerList playerList = this.client.getPlayerList();
		for (GameProfile profile : profiles) {
			if (Objects.requireNonNull(this.accountManager.getAccount()).uniqueId().equals(profile.getUniqueId())) {
				playerList.addPlayer(new LocalPlayer(profile, this.connection));
			} else {
				playerList.addRemotePlayer(profile);
			}
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@PacketListener(PlayerAddPacket.class)
	public void handlePlayerAdd(@NotNull GameProfile profile) {
		ClientPlayerList playerList = this.client.getPlayerList();
		if (Objects.requireNonNull(this.accountManager.getAccount()).uniqueId().equals(profile.getUniqueId())) {
			if (playerList.getPlayer() == null) {
				LOGGER.warn("The local player is not set, that was not supposed to be");
				playerList.addPlayer(new LocalPlayer(profile, this.connection));
			} else {
				LOGGER.warn("The local player is already set to {}, but there is another player with the same id {}", playerList.getPlayer().getProfile(), profile);
			}
		} else {
			playerList.addRemotePlayer(profile);
		}
	}
	
	@PacketListener(PlayerRemovePacket.class)
	public void handlePlayerRemove(@NotNull GameProfile profile) {
		ClientPlayerList playerList = this.client.getPlayerList();
		if (Objects.requireNonNull(this.accountManager.getAccount()).uniqueId().equals(profile.getUniqueId())) {
			playerList.removePlayer(Objects.requireNonNull(playerList.getPlayer(profile)));
		} else {
			playerList.removePlayer(Objects.requireNonNull(playerList.getPlayer(profile)));
		}
	}
	
	@PacketListener(SyncPermissionPacket.class)
	public void handleSyncPermission(@NotNull GameProfile profile) { // TODO: remove and merge with SyncPlayerDataPacket
		//		for (Player player : this.client.getPlayerList().getPlayers()) {
		//			if (player.getProfile().equals(profile)) {
		//				player.setAdmin(true);
		//				LOGGER.debug("Player {} is now a admin", player.getProfile().getName());
		//			} else {
		//				player.setAdmin(false);
		//			}
		//		}
		LOGGER.info("Sync admins");
	}
	
	@PacketListener(SyncPlayerDataPacket.class)
	public void handleSyncPlayerData(@NotNull GameProfile profile, boolean playing, @NotNull PlayerScore score) {
		Player player = this.client.getPlayerList().getPlayer(profile);
		if (player != null) {
			player.getScore().sync(score);
			LOGGER.info("Synchronize data from server of player {}", profile.getName());
		} else {
			LOGGER.warn("Fail to synchronize data from server to player {}, since the player does not exists", profile.getName());
		}
	}
	
	@PacketListener(CancelRollDiceRequestPacket.class)
	public void handleCancelRollDiceRequest() { // TODO: sync required data
		LOGGER.info("Roll dice request was canceled from the server");
		//Objects.requireNonNull(this.client.getPlayerList().getPlayer()).setCanRollDice(false);
	}
	
	@PacketListener(RolledDicePacket.class)
	public void handleRolledDice(int count) { // TODO: sync required data
		//		LocalPlayer player = Objects.requireNonNull(this.client.getPlayerList().getPlayer());
		//		if (Mth.isInBounds(count, 1, 6)) {
		//			player.setCount(count);
		//			player.setCanRollDice(false);
		//		} else {
		//			player.setCount(-1);
		//			player.setCanRollDice(false);
		//		}
		LOGGER.info("Rolled dice with count {}", count);
	}
	
	@PacketListener(CanRollDiceAgainPacket.class)
	public void handleCanRollDiceAgain() { // TODO: sync required data
		LOGGER.info("Can roll dice again");
		//Objects.requireNonNull(this.client.getPlayerList().getPlayer()).setCanRollDice(true);
	}
	
	@PacketListener(CurrentPlayerUpdatePacket.class)
	public void handleCurrentPlayerUpdate(@NotNull GameProfile profile) {
		boolean flag = false;
		GameManager gameManager = this.client.getGameManager();
		if (gameManager.getGameFor(profile) != null) {
			Game game = Objects.requireNonNull(gameManager.getGameFor(profile));
			for (Player player : this.client.getPlayerList().getPlayers()) {
				if (player.getProfile().equals(profile)) {
					game.setPlayer(Objects.requireNonNull(game.getPlayerFor(player)));
					flag = true;
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
	public void handleStartGame(int type, @NotNull List<GamePlayerInfo> playerInfos) {
		GameType<?> gameType = Objects.requireNonNull(GameTypes.fromId(type));
		Game game = gameType.createGame(playerInfos);
		game.start();
		boolean flag = false;
		for (Player player : Utils.mapList(game.getPlayers(), GamePlayer::getPlayer)) {
			if (Objects.requireNonNull(this.client.getPlayerList().getPlayer()).getProfile().equals(player.getProfile())) {
				flag = true;
			}
		}
		if (flag) {
			this.client.setScreen(game.getScreen());
			LOGGER.info("Start game {}", gameType.getInfoName());
			this.client.getGameManager().addGame(game);
		} else {
			LOGGER.warn("Fail to start game {}, since the local player is not in the player list of the game", gameType.getInfoName());
			this.client.setScreen(new LobbyScreen());
		}
	}
	
	@PacketListener(CanSelectGameFieldPacket.class)
	public void handleCanSelectGameField() {
		LocalPlayer player = Objects.requireNonNull(this.client.getPlayerList().getPlayer());
		if (player.isCurrent()) {
			player.setCanSelect(true);
		}
	}
	
	@PacketListener(GameActionFailedPacket.class)
	public void handleGameActionFailed() { // TODO: sync required data
		// Objects.requireNonNull(this.client.getPlayerList().getPlayer()).setCurrent(true);
		LOGGER.info("The game action failed");
	}
	
	@PacketListener(CancelPlayAgainGameRequestPacket.class)
	public void handleCancelPlayAgainGameRequest() {
		LOGGER.warn("The request to play again was canceled by the server");
	}
	
	@PacketListener(ExitGamePacket.class)
	public void handleExitGame() {
		LOGGER.info("Exit the current game");
		LocalPlayer player = Objects.requireNonNull(this.client.getPlayerList().getPlayer());
		if (player.isPlaying()) {
			player.getScore().reset();
			GameManager gameManager = this.client.getGameManager();
			if (gameManager.getGameFor(player.getProfile()) != null) {
				gameManager.removeGame(Objects.requireNonNull(gameManager.getGameFor(player.getProfile())));
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
		for (Player player : this.client.getPlayerList().getPlayers()) {
			player.getScore().reset();
		}
		LocalPlayer player = Objects.requireNonNull(this.client.getPlayerList().getPlayer());
		GameManager gameManager = this.client.getGameManager();
		if (gameManager.getGameFor(player.getProfile()) != null) {
			gameManager.removeGame(Objects.requireNonNull(gameManager.getGameFor(player.getProfile())));
		} else {
			LOGGER.warn("Received a stop game packet, but there is no active game");
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@PacketListener(ServerClosedPacket.class)
	public void handleServerClosed() {
		this.client.getNetworkInstance().close();
		this.client.getPlayerList().removePlayer(Objects.requireNonNull(this.client.getPlayerList().getPlayer()));
		this.client.setScreen(new MenuScreen());
	}
}