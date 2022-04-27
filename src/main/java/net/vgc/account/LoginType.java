package net.vgc.account;

import javax.annotation.Nullable;

public enum LoginType {
	
	REGISTRATION("registration"),
	USER_LOGIN("login_user"),
	GUEST_LOGIN("login_guest"),
	UNKNOWN("unknown");
	
	private final String name;
	
	private LoginType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Nullable
	public static LoginType fromName(String name) {
		for (LoginType result : LoginType.values()) {
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return UNKNOWN;
	}
	
}
