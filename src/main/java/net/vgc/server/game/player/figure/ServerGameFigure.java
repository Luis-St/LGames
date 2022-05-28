package net.vgc.server.game.player.figure;

import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.player.ServerGamePlayer;

public interface ServerGameFigure extends GameFigure, PacketHandler<ServerPacket> {
	
	@Override
	ServerGamePlayer getPlayer();
	
}
