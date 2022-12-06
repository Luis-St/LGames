package net.vgc.network;

import net.vgc.account.AccountServer;
import net.vgc.client.Client;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketListener;
import net.vgc.network.packet.account.AccountPacket;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.Server;
import net.vgc.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum NetworkSide implements EnumRepresentable, PacketListener<Packet<?>> {
	

	CLIENT("client", 0) {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof ClientPacket clientPacket) {
				Client.getInstance().handlePacket(clientPacket);
			}
		}
	},
	SERVER("server", 1) {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof ServerPacket serverPacket) {
				Server.getInstance().handlePacket(serverPacket);
			}
		}
	},
	ACCOUNT("account", 2) {
		@Override
		public void handlePacket(Packet<?> packet) {
			if (this.isOn() && packet instanceof AccountPacket accountPacket) {
				AccountServer.getInstance().handlePacket(accountPacket);
			}
		}
	};
	
	private final String name;
	private final int id;
	
	private NetworkSide(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public boolean isOn() {
		return Network.INSTANCE.getNetworkSide() == this;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
