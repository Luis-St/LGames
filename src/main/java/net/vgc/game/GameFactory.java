package net.vgc.game;

import java.util.List;
import java.util.function.BiFunction;

import net.vgc.client.Client;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;

public class GameFactory<S extends Game, C extends Game> {
	
	protected final BiFunction<DedicatedServer, List<ServerPlayer>, S> serverFactory;
	protected final BiFunction<Client, List<GamePlayerInfo>, C> clientFactory;
	
	public GameFactory(BiFunction<DedicatedServer, List<ServerPlayer>, S> serverFactory, BiFunction<Client, List<GamePlayerInfo>, C> clientFactory) {
		this.serverFactory = serverFactory;
		this.clientFactory = clientFactory;
	}
	
	public S createServerGame(DedicatedServer server, List<ServerPlayer> players) {
		S game = this.serverFactory.apply(server, players);
		game.initGame();
		return game;
	}
	
	public C createClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		C game = this.clientFactory.apply(client, playerInfos);
		game.initGame();
		return game;
	}
	
}
