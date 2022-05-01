package net.vgc.network;

import net.vgc.account.AccountServer;
import net.vgc.client.Client;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.account.AccountPacket;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.Server;

public enum NetworkSide implements PacketHandler<Packet<?>> {
	
	CLIENT("client") {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof ClientPacket clientPacket) {
				Client.getInstance().handlePacket(clientPacket);
			}
		}
	},
	SERVER("server") {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof ServerPacket serverPacket) {
				Server.getInstance().handlePacket(serverPacket);
			}
		}
	},
	ACCOUNT_SERVER("account") {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof AccountPacket accountPacket) {
				AccountServer.getInstance().handlePacket(accountPacket);
			}
		}
	};
	
	private final String name;
	
	private NetworkSide(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isOn() {
		return Network.INSTANCE.getNetworkSide() == this;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
