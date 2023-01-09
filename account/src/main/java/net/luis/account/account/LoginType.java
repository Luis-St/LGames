package net.luis.account.account;

import net.luis.common.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum LoginType implements EnumRepresentable {
	
	REGISTRATION("registration", 0), USER_LOGIN("login_user", 1), GUEST_LOGIN("login_guest", 2), UNKNOWN("unknown", 3);
	
	private final String name;
	private final int id;
	
	LoginType(String name, int id) {
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
	
	@Override
	public Enum<LoginType> getDefault() {
		return UNKNOWN;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
