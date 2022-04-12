package net.vgc.network;

import net.vgc.Constans;

public enum NetworkSide {
	
	CLIENT("client"),
	SERVER("server"),
	ACCOUNT_SERVER("account");
	
	private final String name;
	
	private NetworkSide(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isOn() {
		return Network.INSTANCE.getNetworkSide() == this && Constans.LAUNCH_TYPE.equals(this.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
