package net.vgc.network;

import java.nio.file.Path;

import net.vgc.client.Client;
import net.vgc.server.account.AccountServer;

public class Network {
	
	public static final Network INSTANCE = new Network();
	
	protected NetworkSide networkSide;
	
	private Network() {
		
	}
	
	public NetworkSide getNetworkSide() {
		return networkSide;
	}
	
	public void setNetworkSide(NetworkSide networkSide) {
		this.networkSide = networkSide;
	}
	
	public Path getGameDirectory() {
		if (NetworkSide.CLIENT.isOn()) {
			return Client.getInstance().getResourceDirectory();
		} else if (NetworkSide.SERVER.isOn()) {
			throw new UnsupportedOperationException("Not implemeted yet"); // TODO: add server side path
		} else if (NetworkSide.ACCOUNT_SERVER.isOn()) {
			return AccountServer.getInstance().getResourceDirectory();
		}
		throw new IllegalStateException("Unknown network side: " + this.getNetworkSide());
	}
	
	public Path getResourceDirectory() {
		if (NetworkSide.CLIENT.isOn()) {
			return Client.getInstance().getResourceDirectory();
		} else if (NetworkSide.SERVER.isOn()) {
			throw new UnsupportedOperationException("Not implemeted yet"); // TODO: add server side path
		} else if (NetworkSide.ACCOUNT_SERVER.isOn()) {
			return AccountServer.getInstance().getResourceDirectory();
		}
		throw new IllegalStateException("Unknown network side: " + this.getNetworkSide());
	}
	
	public void executeOn(NetworkSide networkSide, Runnable action) {
		if (networkSide.isOn()) {
			action.run();
		}
	}
	
}
