package net.luis.application;

import java.util.Objects;

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
		if (application == null) {
			throw new NullPointerException("Cannot check application type because \"application\" is null");
		}
		return Objects.requireNonNull(application.getApplicationType()) == this;
	}
	
	@Deprecated
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
