package net.vgc.server.game.map.field;

import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.player.figure.ServerGameFigure;

public interface ServerGameField extends GameField, PacketHandler<ServerPacket> {
	
	@Override
	GameFieldType getFieldType();
	
	@Override
	GameFieldPos getFieldPos();
	
	@Override
	boolean isHome();
	
	@Override
	boolean isStart();
	
	@Override
	boolean isStartFor(GameFigure figure);
	
	@Override
	boolean isWin();
	
	@Override
	ServerGameFigure getFigure();
	
	@Override
	void setFigure(GameFigure figure);
	
	@Override
	void handlePacket(ServerPacket serverPacket);
	
}
