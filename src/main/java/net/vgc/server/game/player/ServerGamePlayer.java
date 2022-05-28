package net.vgc.server.game.player;

import java.util.List;

import net.vgc.game.player.GamePlayer;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.server.game.player.figure.ServerGameFigure;
import net.vgc.server.player.ServerPlayer;

public interface ServerGamePlayer extends GamePlayer, PacketHandler<ServerPacket> {
	
	@Override
	ServerGame getGame();
	
	@Override
	ServerPlayer getPlayer();
	
	@Override
	default ServerGameMap getMap() {
		return this.getGame().getMap();
	}
	
	@Override
	List<? extends ServerGameFigure> getFigures();
	
	@Override
	ServerGameFigure getFigure(int figure);
	
	@Override
	default void handlePacket(ServerPacket packet) {
		for (ServerGameFigure figure : this.getFigures()) {
			figure.handlePacket(packet);
		}
	}
	
}
