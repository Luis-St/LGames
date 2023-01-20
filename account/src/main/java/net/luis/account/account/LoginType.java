package net.luis.account.account;

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
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
