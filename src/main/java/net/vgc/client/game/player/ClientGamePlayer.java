package net.vgc.client.game.player;

import java.util.List;

import net.vgc.client.game.ClientGame;
import net.vgc.client.game.map.ClientGameMap;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public interface ClientGamePlayer extends GamePlayer, PacketHandler<ClientPacket> {
	
	@Override
	ClientGame getGame();
	
	@Override
	AbstractClientPlayer getPlayer();
	
	@Override
	default ClientGameMap getMap() {
		return this.getGame().getMap();
	}
	
	@Override
	List<? extends ClientGameFigure> getFigures();
	
	@Override
	ClientGameFigure getFigure(int figure);
	
	@Override
	default void setRollCount(int rollCount) {
		LOGGER.warn("Can not set the roll count of player, on client", this.getPlayer().getProfile().getName());
	}
	
	@Override
	default void handlePacket(ClientPacket packet) {
		for (ClientGameFigure figure : this.getFigures()) {
			figure.handlePacket(packet);
		}
	}
	
}
