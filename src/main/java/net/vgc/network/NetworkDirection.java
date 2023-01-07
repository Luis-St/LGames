package net.vgc.network;

import net.vgc.network.packet.Packet;
import net.vgc.network.packet.account.AccountPacket;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.server.ServerPacket;

/**
 *
 * @author Luis-st
 *
 */

public enum NetworkDirection {
	
	SERVER_TO_CLIENT("server_to_client", NetworkSide.SERVER, NetworkSide.CLIENT) {
		@Override
		public boolean canSendPacket(Packet packet) {
			return packet instanceof ClientPacket;
		}
	},
	CLIENT_TO_SERVER("client_to_server", NetworkSide.CLIENT, NetworkSide.SERVER) {
		@Override
		public boolean canSendPacket(Packet packet) {
			return packet instanceof ServerPacket;
		}
	},
	ACCOUNT_TO_CLIENT("account_to_client", NetworkSide.ACCOUNT, NetworkSide.CLIENT) {
		@Override
		public boolean canSendPacket(Packet packet) {
			return packet instanceof ClientPacket;
		}
	},
	CLIENT_TO_ACCOUNT("client_to_account", NetworkSide.CLIENT, NetworkSide.ACCOUNT) {
		@Override
		public boolean canSendPacket(Packet packet) {
			return packet instanceof AccountPacket;
		}
	};
	
	private final String name;
	private final NetworkSide from;
	private final NetworkSide to;
	
	NetworkDirection(String name, NetworkSide from, NetworkSide to) {
		this.name = name;
		this.from = from;
		this.to = to;
	}
	
	public String getName() {
		return this.name;
	}
	
	public NetworkSide getFrom() {
		return this.from;
	}
	
	public NetworkSide getTo() {
		return this.to;
	}
	
	public abstract boolean canSendPacket(Packet packet);
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
