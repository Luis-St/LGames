package net.vgc.client.game.games.ttt;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import net.vgc.client.Client;
import net.vgc.client.game.ClientGame;
import net.vgc.client.game.games.ttt.map.TTTClientMap;
import net.vgc.client.game.games.ttt.player.TTTClientPlayer;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.ttt.TTTServerGame;

public class TTTClientGame implements ClientGame {
	
	protected final Client client;
	protected final List<TTTClientPlayer> players;
	protected final TTTClientMap map;
	protected TTTClientPlayer player;
	
	public TTTClientGame(Client client, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		this.client = client;
		this.players = createGamePlayers(this.client, this, playerInfos);
		this.map = new TTTClientMap(this.client, this);
	}
	
	protected static List<TTTClientPlayer> createGamePlayers(Client client, TTTClientGame game, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		List<TTTClientPlayer> gamePlayers = Lists.newArrayList();
		for (Cell<GameProfile, GamePlayerType, List<UUID>> cell : playerInfos) {
			AbstractClientPlayer player = client.getPlayer(cell.getRowKey());
			if (player != null) {
				gamePlayers.add(new TTTClientPlayer(game, player, (TTTPlayerType) cell.getColumnKey(), cell.getValue()));
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
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}

	@Override
	public TTTClientMap getMap() {
		return this.map;
	}

	@Override
	public List<TTTClientPlayer> getPlayers() {
		return this.players;
	}

	@Override
	public ClientGamePlayer getCurrentPlayer() {
		return this.player;
	}
	
	@Override
	public void setCurrentPlayer(GamePlayer player) {
		this.player = (TTTClientPlayer) player;
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
		return "TTTClientGame";
	}

}
