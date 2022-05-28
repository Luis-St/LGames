package net.vgc.server.game.map.field;

import net.vgc.game.map.field.GameField;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.player.figure.ServerGameFigure;

public interface ServerGameField extends GameField, PacketHandler<ServerPacket> {
	
	@Override
	ServerGameFigure getFigure();
	
}
