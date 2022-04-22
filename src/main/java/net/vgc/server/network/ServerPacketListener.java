package net.vgc.server.network;

import java.util.UUID;

import net.vgc.network.NetworkSide;
import net.vgc.network.packet.AbstractPacketListener;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;

public class ServerPacketListener extends AbstractPacketListener {
	
	protected final DedicatedServer server;
	
	public ServerPacketListener(DedicatedServer server, NetworkSide networkSide) {
		super(networkSide);
		this.server = server;
	}
	
	public void handleClientJoin(String name, UUID uuid) {
		this.checkSide();
		this.server.enterPlayer(this.connection, new GameProfile(name, uuid));
		this.connection.send(new ClientJoinedPacket(this.server.getPlayerList().getPlayers()));
	}
	
	public void handleClientLeave(UUID uuid) {
		this.checkSide();
		this.server.leavePlayer(this.connection, this.server.getPlayerList().getPlayer(uuid));
	}
	
}
