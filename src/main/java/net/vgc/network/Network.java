package net.vgc.network;

import java.nio.file.Path;

import net.vgc.account.AccountServer;
import net.vgc.client.Client;
import net.vgc.server.Server;

/**
 *
 * @author Luis-st
 *
 */

public class Network {
	
	public static final Network INSTANCE = new Network();
	
	private NetworkSide networkSide;
	
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
			return Server.getInstance().getResourceDirectory();
		} else if (NetworkSide.ACCOUNT.isOn()) {
			return AccountServer.getInstance().getResourceDirectory();
		}
		throw new IllegalStateException("Unknown network side: " + this.getNetworkSide());
	}
	
	public Path getResourceDirectory() {
		if (NetworkSide.CLIENT.isOn()) {
			return Client.getInstance().getResourceDirectory();
		} else if (NetworkSide.SERVER.isOn()) {
			return Server.getInstance().getResourceDirectory();
		} else if (NetworkSide.ACCOUNT.isOn()) {
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
