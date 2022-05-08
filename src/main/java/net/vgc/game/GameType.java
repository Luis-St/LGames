package net.vgc.game;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.client.Client;
import net.vgc.client.screen.game.GameScreen;
import net.vgc.network.NetworkSide;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class GameType<T extends Game> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final String name;
	protected final int minPlayers;
	protected final int maxPlayers;
	protected final Function<List<ServerPlayer>, T> createFunction;
	protected final Supplier<? extends GameScreen> screenSupplier;
	
	public GameType(String name, int minPlayers, int maxPlayers, Function<List<ServerPlayer>, T> createFunction, Supplier<? extends GameScreen> screenSupplier) {
		this.name = name;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.createFunction = createFunction;
		this.screenSupplier = screenSupplier;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getMinPlayers() {
		return this.minPlayers;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public boolean enoughPlayersToPlay(List<ServerPlayer> players) {
		if (this.maxPlayers >= players.size() && players.size() >= this.minPlayers) {
			return true;
		}
		return false;
	}
	
	@Nullable
	public T createNewGame(List<ServerPlayer> players) {
		if (this.enoughPlayersToPlay(players)) {
			LOGGER.info("Start game with players {}", players.stream().map(ServerPlayer::getGameProfile).map(GameProfile::getName).collect(Collectors.toList()));
			T game = this.createFunction.apply(players);
			game.onStart();
			return game;
		}
		LOGGER.warn("Fail to create new game of type {}, since the player count {} is out of bounds {} - {} ", this.name, players.size(), this.minPlayers, this.maxPlayers);
		return null;
	}
	
	public void openScreen() {
		if (NetworkSide.CLIENT.isOn()) {
			Client.getInstance().setScreen(this.screenSupplier.get());
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
