package net.vgc.client.game.games.wins4;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.client.Client;
import net.vgc.client.game.ClientGame;
import net.vgc.client.game.games.wins4.map.Wins4ClientMap;
import net.vgc.client.game.games.wins4.player.Wins4ClientPlayer;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.wins4.Wins4ServerGame;
import net.vgc.util.Util;

public class Wins4ClientGame implements ClientGame {
	
	protected final Client client;
	protected final List<Wins4ClientPlayer> players;
	protected final Wins4ClientMap map;
	protected Wins4ClientPlayer player;
	
	public Wins4ClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		this.client = client;
		this.players = createGamePlayers(this.client, this, playerInfos);
		this.map = new Wins4ClientMap(this.client, this);
	}
	
	protected static List<Wins4ClientPlayer> createGamePlayers(Client client, Wins4ClientGame game, List<GamePlayerInfo> playerInfos) {
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(playerInfos, GamePlayerInfo::getProfile, GameProfile::getName));
		List<Wins4ClientPlayer> gamePlayers = Lists.newArrayList();
		for (GamePlayerInfo playerInfo : playerInfos) {
			AbstractClientPlayer player = client.getPlayer(playerInfo.getProfile());
			if (player != null) {
				gamePlayers.add(new Wins4ClientPlayer(game, player, (Wins4PlayerType) playerInfo.getPlayerType(), playerInfo.getUUIDs()));
			} else {
				LOGGER.warn("Fail to create game player for player {}, since the player does not exists on the client", playerInfo.getProfile().getName());
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
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}

	@Override
	public Wins4ClientMap getMap() {
		return this.map;
	}

	@Override
	public List<Wins4ClientPlayer> getPlayers() {
		return this.players;
	}

	@Override
	public Wins4ClientPlayer getCurrentPlayer() {
		return this.player;
	}
	
	@Override
	public void setCurrentPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.player, this::getName), Util.runIfNotNull(player, this::getName));
		this.player = (Wins4ClientPlayer) player;
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
		return "Win4ClientGame";
	}
	
}
