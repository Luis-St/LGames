package net.vgc.client.game.player;

import java.util.List;

import net.vgc.client.game.ClientGame;
import net.vgc.client.game.map.ClientGameMap;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;

public interface ClientGamePlayer extends GamePlayer {
	
	@Override
	ClientGame getGame();
	
	@Override
	AbstractClientPlayer getPlayer();
	
	@Override
	GamePlayerType getPlayerType();
	
	@Override
	default ClientGameMap getMap() {
		return this.getGame().getMap();
	}
	
	@Override
	List<? extends ClientGameFigure> getFigures();
	
	@Override
	ClientGameFigure getFigure(int figure);
	
	@Override
	List<? extends GameFieldPos> getWinPoses();
	
	@Override
	default int getRollCount() {
		LOGGER.warn("Can not get the roll count of player {}, on client", this.getPlayer().getProfile().getName());
		return -1;
	}
	
	@Override
	default void setRollCount(int rollCount) {
		LOGGER.warn("Can not set the roll count of player {}, on client", this.getPlayer().getProfile().getName());
	}
	
}
