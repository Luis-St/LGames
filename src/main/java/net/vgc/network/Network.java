package net.vgc.network;

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
	
	public void executeOn(NetworkSide networkSide, Runnable action) {
		if (networkSide.isOn()) {
			action.run();
		}
	}
	
}
