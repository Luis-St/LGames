package net.vgc.client.game.player.figure;

import net.vgc.client.game.player.ClientGamePlayer;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public interface ClientGameFigure extends GameFigure, PacketHandler<ClientPacket> {
	
	@Override
	ClientGamePlayer getPlayer();
	
}
