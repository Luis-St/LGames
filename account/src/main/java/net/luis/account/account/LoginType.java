package net.luis.account.account;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public enum LoginType {
	
	REGISTRATION("registration"), USER_LOGIN("login_user"), GUEST_LOGIN("login_guest"), UNKNOWN("unknown");
	
	private final String name;
	
	LoginType(String name) {
		this.name = name;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
}
