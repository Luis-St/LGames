package net.vgc.game;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.common.collect.Table.Cell;

import net.vgc.client.Client;
import net.vgc.client.game.ClientGame;
import net.vgc.game.player.GamePlayerType;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.player.ServerPlayer;

public class GameFactory<S extends ServerGame, C extends ClientGame> {
	
	protected final BiFunction<DedicatedServer, List<ServerPlayer>, S> serverFactory;
	protected final BiFunction<Client, List<Cell<GameProfile, GamePlayerType, List<UUID>>>, C> clientFactory;
	
	public GameFactory(BiFunction<DedicatedServer, List<ServerPlayer>, S> serverFactory, BiFunction<Client, List<Cell<GameProfile, GamePlayerType, List<UUID>>>, C> clientFactory) {
		this.serverFactory = serverFactory;
		this.clientFactory = clientFactory;
	}
	
	public S createServerGame(DedicatedServer server, List<ServerPlayer> players) {
		S game = this.serverFactory.apply(server, players);
		game.initGame();
		return game;
	}
	
	public C createClientGame(Client client, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		C game = this.clientFactory.apply(client, playerInfos);
		game.initGame();
		return game;
	}
	
}
