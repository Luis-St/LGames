package net.vgc.game;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.client.Client;
import net.vgc.client.screen.game.GameScreen;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.network.NetworkSide;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;

public class GameType<S extends Game, C extends Game> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final String name;
	protected final int minPlayers;
	protected final int maxPlayers;
	protected final GameFactory<S, C> gameFactory;
	protected final Function<C, ? extends GameScreen> screenFactory;
	
	public GameType(String name, int minPlayers, int maxPlayers, GameFactory<S, C> gameFactory, Function<C, ? extends GameScreen> screenFactory) {
		this.name = name;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.gameFactory = gameFactory;
		this.screenFactory = screenFactory;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getInfoName() {
		return this.name.toLowerCase();
	}
	
	public int getMinPlayers() {
		return this.minPlayers;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public boolean hasEnoughPlayers(int players) {
		return Mth.isInBounds(players, this.minPlayers, this.maxPlayers);
	}
	
	public boolean hasEnoughPlayers(List<GamePlayer> players) {
		return this.hasEnoughPlayers(players.size());
	}
	
	public String getBounds() {
		return this.minPlayers + " - " + this.maxPlayers;
	}
	
	@Nullable
	public S createServerGame(DedicatedServer server, List<ServerPlayer> players) {
		if (this.hasEnoughPlayers(players.size())) {
			return this.gameFactory.createServerGame(server, players);
		}
		return null;
	}
	
	public C createClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		if (this.hasEnoughPlayers(playerInfos.size())) {
			return this.gameFactory.createClientGame(client, playerInfos);
		}
		return null;
	}
	
	public void openScreen(Client client, C game) {
		if (NetworkSide.CLIENT.isOn()) {
			client.setScreen(this.screenFactory.apply(game));
		}
	}
	
	@Override
	public String toString() {
		return this.getInfoName();
	}
	
}
