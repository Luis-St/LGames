package net.vgc.client.game.player.figure;

import java.util.UUID;

import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public interface ClientGameFigure extends GameFigure, PacketHandler<ClientPacket> {
	
	@Override
	ClientGamePlayer getPlayer();
	
	@Override
	GamePlayerType getPlayerType();
	
	@Override
	int getCount();
	
	@Override
	UUID getUUID();
	
	@Override
	GameFieldPos getHomePos();
	
	@Override
	GameFieldPos getStartPos();
	
}
