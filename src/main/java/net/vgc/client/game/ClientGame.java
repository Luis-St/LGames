package net.vgc.client.game;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.client.Client;
import net.vgc.client.game.map.ClientGameMap;
import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.dice.DiceHandler;

public interface ClientGame extends Game, PacketHandler<ClientPacket> {

	@Override
	void initGame();
	
	@Override
	void startGame();
	
	Client getClient();
	
	@Override
	GameType<? extends ServerGame, ? extends ClientGame> getType();
	
	@Override
	ClientGameMap getMap();
	
	@Override
	List<? extends ClientGamePlayer> getPlayers();
	
	@Override
	default List<? extends ClientGamePlayer> getEnemies(GamePlayer player) {
		List<ClientGamePlayer> enemies = Lists.newArrayList();
		for (ClientGamePlayer gamePlayer : this.getPlayers()) {
			if (!gamePlayer.equals(player)) {
				enemies.add(gamePlayer);
			}
		}
		if (enemies.isEmpty()) {
			LOGGER.warn("Fail to get enemies for player {}", this.getName(player));
		}
		return enemies;
	}
	
	@Override
	default ClientGamePlayer getPlayerFor(Player player) {
		for (ClientGamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().equals(player)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Override
	default ClientGamePlayer getPlayerFor(GameProfile profile) {
		for (ClientGamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().getProfile().equals(profile)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Override
	ClientGamePlayer getCurrentPlayer();
	
	@Nullable
	@Override
	default GamePlayer getStartPlayer() {
		LOGGER.warn("Can not get the start player on client");
		return null;
	}
	
	@Override
	default void nextPlayer(boolean random) {
		LOGGER.warn("Can not set the next player on client");
	}
	
	@Override
	default boolean removePlayer(GamePlayer player, boolean sendExit) {
		LOGGER.warn("Can not remove player {} from game {} on client", player.getPlayer().getProfile().getName(), this.getType().getInfoName());
		return false;
	}
	
	@Override
	default DiceHandler getDiceHandler() {
		LOGGER.warn("Can not get the dice handler on client");
		return null;
	}
	
	@Override
	default boolean nextMatch() {
		return false;
	}
	
	@Override
	void stopGame();
	
	@Override
	default void handlePacket(ClientPacket packet) {
		this.getMap().handlePacket(packet);
		for (ClientGamePlayer player : this.getPlayers()) {
			player.handlePacket(packet);
		}
	}
	
}
