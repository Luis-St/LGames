package net.vgc.server.games.ludo;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.vgc.client.games.ludo.LudoClientGame;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ludo.action.LudoServerActionHandler;
import net.vgc.server.games.ludo.dice.LudoDiceHandler;
import net.vgc.server.games.ludo.map.LudoServerMap;
import net.vgc.server.games.ludo.player.LudoServerPlayer;
import net.vgc.server.games.ludo.win.LudoWinHandler;
import net.vgc.server.player.ServerPlayer;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerGame extends AbstractServerGame {
	
	private final LudoDiceHandler diceHandler;
	
	public LudoServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, LudoServerMap::new, players, LudoPlayerType.values(), (game, player, playerType) -> {
			return new LudoServerPlayer(game, player, playerType, 4);
		}, new LudoWinHandler(), LudoServerActionHandler::new);
		this.diceHandler = new LudoDiceHandler(this, 1, 6);
	}
	
	@Override
	public GameType<LudoServerGame, LudoClientGame> getType() {
		return GameTypes.LUDO;
	}
	
	@Override
	public void nextPlayer(boolean random) {
		List<? extends GamePlayer> players = Lists.newArrayList(this.getPlayers());
		players.removeIf(this.getWinHandler().getWinOrder()::contains);
		if (!players.isEmpty()) {
			if (random) {
				this.setPlayer(players.get(new Random().nextInt(players.size())));
			} else {
				GamePlayer player = this.getPlayer();
				if (player == null) {
					this.setPlayer(players.get(0));
				} else {
					int index = players.indexOf(player);
					if (index != -1) {
						index++;
						if (index >= players.size()) {
							this.setPlayer(players.get(0));
						} else {
							this.setPlayer(players.get(index));
						}
					} else {
						LOGGER.warn("Fail to get next player, since the player {} does not exists", player.getName());
						this.setPlayer(players.get(0));
					}
				}
			}
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
		}
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Override
	public DiceHandler getDiceHandler() {
		return this.diceHandler;
	}
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
