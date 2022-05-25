package net.vgc.game;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.client.Client;
import net.vgc.client.screen.game.GameScreen;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;

public class GameType<T extends Game> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final String name;
	protected final int minPlayers;
	protected final int maxPlayers;
	protected final Function<List<ServerPlayer>, T> gameFactory;
	protected final Function<T, ClientPacket> packetFactory;
	protected final Function<T, ? extends GameScreen> screenFactory;
	
	public GameType(String name, int minPlayers, int maxPlayers, Function<List<ServerPlayer>, T> gameFactory, Function<T, ClientPacket> packetFactory, Function<T, ? extends GameScreen> screenFactory) {
		this.name = name;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.gameFactory = gameFactory;
		this.packetFactory = packetFactory;
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
	
	public boolean hasEnoughPlayers(List<ServerPlayer> players) {
		return Mth.isInBounds(players.size(), this.minPlayers, this.maxPlayers);
	}
	
	@Nullable
	public T createGame(List<ServerPlayer> players) {
		if (this.hasEnoughPlayers(players)) {
			T game = this.gameFactory.apply(players);
			game.initGame();
			game.startGame();
			return game;
		}
		return null;
	}
	
	public ClientPacket getStartPacket(T game) {
		return this.packetFactory.apply(game);
	}
	
	public void openScreen(Client client, T game) {
		if (NetworkSide.CLIENT.isOn()) {
			client.setScreen(this.screenFactory.apply(game));
		}
	}
	
	@Override
	public String toString() {
		return this.getInfoName();
	}
	
}
