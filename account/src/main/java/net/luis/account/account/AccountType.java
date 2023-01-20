package net.luis.account.account;

import net.luis.language.TranslationKey;

/**
 *
 * @author Luis-st
 *
 */

public enum AccountType {
	
	USER("user"), GUEST("guest"), TEST("test"), UNKNOWN("unknown");
	
	private final String name;
	
	AccountType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getTranslation() {
		return TranslationKey.createAndGet("account.type." + this.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
