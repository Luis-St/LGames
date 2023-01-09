package net.luis.network;

import net.luis.common.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum NetworkSide implements EnumRepresentable {
	
	CLIENT("client", 0), SERVER("server", 1), ACCOUNT("account", 2);
	
	private final String name;
	private final int id;
	
	NetworkSide(String name, int id) {
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
