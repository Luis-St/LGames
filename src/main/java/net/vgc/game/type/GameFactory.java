package net.vgc.game.type;

import net.vgc.client.Client;
import net.vgc.game.Game;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.server.Server;
import net.vgc.server.player.ServerPlayer;

import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @author Luis-st
 *
 */

public class GameFactory<S extends Game, C extends Game> {
	
	private final BiFunction<Server, List<ServerPlayer>, S> serverFactory;
	private final BiFunction<Client, List<GamePlayerInfo>, C> clientFactory;
	
	public GameFactory(BiFunction<Server, List<ServerPlayer>, S> serverFactory, BiFunction<Client, List<GamePlayerInfo>, C> clientFactory) {
		this.serverFactory = serverFactory;
		this.clientFactory = clientFactory;
	}
	
	public S createServerGame(Server server, List<ServerPlayer> players) {
		S game = this.serverFactory.apply(server, players);
		game.init();
		return game;
	}
	
	public C createClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		C game = this.clientFactory.apply(client, playerInfos);
		game.init();
		return game;
	}
	
}
