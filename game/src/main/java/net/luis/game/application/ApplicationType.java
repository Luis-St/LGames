package net.luis.game.application;

import org.jetbrains.annotations.NotNull;

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
	
	ApplicationType(@NotNull String name, @NotNull String shortName) {
		this.name = name;
		this.shortName = shortName;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull String getShortName() {
		return this.shortName;
	}
	
	public boolean isClient() {
		return this == CLIENT;
	}
	
	public boolean isServer() {
		return this == SERVER || this == ACCOUNT;
	}
	
	public boolean isOn() {
		FxApplication application = FxApplication.getInstance();
		if (application == null) {
			throw new NullPointerException("Cannot check application type because \"application\" is null");
		}
		return Objects.requireNonNull(application.getType()) == this;
	}
	
	@Deprecated
	public void executeIfOn(@NotNull Runnable action) {
		if (this.isOn()) {
			action.run();
		}
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
	
}
