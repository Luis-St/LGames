package net.luis.application;

import net.luis.account.AccountServer;
import net.luis.client.Client;
import net.luis.server.Server;

/**
 *
 * @author Luis-st
 *
 */

public enum ApplicationType {
	
	CLIENT("virtual game collection", "client"), SERVER("account server", "server"), ACCOUNT("virtual game collection server", "account");
	
	private final String name;
	private final String shortName;
	
	ApplicationType(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public boolean isOn() {
		GameApplication application = GameApplication.getInstance();
		if (this == CLIENT) {
			return application instanceof Client;
		} else if (this == SERVER) {
			return application instanceof Server;
		} else if (this == ACCOUNT) {
			return application instanceof AccountServer;
		}
		return false;
	}
	
	public void executeIfOn(Runnable action) {
		if (this.isOn()) {
			action.run();
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
