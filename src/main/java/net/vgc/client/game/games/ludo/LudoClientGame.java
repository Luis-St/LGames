package net.vgc.client.game.games.ludo;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import net.vgc.client.Client;
import net.vgc.client.game.ClientGame;
import net.vgc.client.game.games.ludo.map.LudoClientMap;
import net.vgc.client.game.games.ludo.player.LudoClientPlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.util.Util;

public class LudoClientGame implements ClientGame {
	
	protected final Client client;
	protected final List<LudoClientPlayer> players;
	protected final LudoClientMap map;
	protected LudoClientPlayer player;
	
	public LudoClientGame(Client client, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		this.client = client;
		this.players = createGamePlayers(this.client, this, playerInfos);
		this.map = new LudoClientMap(this.client, this);
	}
	
	protected static List<LudoClientPlayer> createGamePlayers(Client client, LudoClientGame game, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(playerInfos, Cell::getRowKey, GameProfile::getName));
		List<LudoClientPlayer> gamePlayers = Lists.newArrayList();
		for (Cell<GameProfile, GamePlayerType, List<UUID>> cell : playerInfos) {
			AbstractClientPlayer player = client.getPlayer(cell.getRowKey());
			if (player != null) {
				gamePlayers.add(new LudoClientPlayer(game, player, (LudoPlayerType) cell.getColumnKey(), cell.getValue()));
			} else {
				LOGGER.warn("Fail to create game player for player {}, since the player does not exists on the client", cell.getRowKey().getName());
			}
		}
		return gamePlayers;
	}

	@Override
	public void initGame() {
		this.map.init(this.players);
	}

	@Override
	public void startGame() {
		
	}
	
	@Override
	public Client getClient() {
		return this.client;
	}

	@Override
	public GameType<LudoServerGame, LudoClientGame> getType() {
		return GameTypes.LUDO;
	}

	@Override
	public LudoClientMap getMap() {
		return this.map;
	}

	@Override
	public List<LudoClientPlayer> getPlayers() {
		return this.players;
	}

	@Override
	public LudoClientPlayer getCurrentPlayer() {
		return this.player;
	}
	
	@Override
	public void setCurrentPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.player, this::getName), Util.runIfNotNull(player, this::getName));
		this.player = (LudoClientPlayer) player;
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Override
	public void stopGame() {
		LOGGER.info("Stopping the current game {}", this.getType().getInfoName());
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			player.setPlaying(false);
			player.getScore().reset();
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@Override
	public void handlePacket(ClientPacket packet) {
		ClientGame.super.handlePacket(packet);
		
	}
	
	@Override
	public String toString() {
		return "LudoClientGame";
	}

}
