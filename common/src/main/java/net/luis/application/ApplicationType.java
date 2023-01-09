package net.luis.application;

import net.luis.account.AccountServer;
import net.luis.client.Client;
import net.luis.server.Server;
import net.luis.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum ApplicationType implements EnumRepresentable {
	
	CLIENT("virtual game collection", "client", 0), SERVER("account server", "server", 1), ACCOUNT("virtual game collection server", "account", 2);
	
	private final String name;
	private final String shortName;
	private final int id;
	
	ApplicationType(String name, String shortName, int id) {
		this.name = name;
		this.shortName = shortName;
		this.id = id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	@Override
	public int getId() {
		return this.id;
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
