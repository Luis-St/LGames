package net.vgc.server.dedicated;

import net.vgc.network.Connection;
import net.vgc.network.packet.client.ClientPlayerAddPacket;
import net.vgc.network.packet.client.ClientPlayerRemovePacket;
import net.vgc.server.player.ServerPlayer;
import net.vgc.server.players.PlayerList;

public class DedicatedPlayerList extends PlayerList {

	public DedicatedPlayerList(DedicatedServer server) {
		super(server);
	}
	
	protected void refreshPlayers() {
		((DedicatedServer) this.server).refreshPlayers();
	}
	
	@Override
	public void addPlayer(Connection connection, ServerPlayer player) {
		super.addPlayer(connection, player);
		this.refreshPlayers();
		this.broadcastAllExclude(new ClientPlayerAddPacket(player.getGameProfile()), player);
	}
	
	@Override
	public void removePlayer(ServerPlayer player) {
		super.removePlayer(player);
		this.refreshPlayers();
		this.broadcastAll(new ClientPlayerRemovePacket(player.getGameProfile()));
	}
	
	@Override
	public void removeAllPlayers() {
		super.removeAllPlayers();
		this.refreshPlayers();
	}
	
}
