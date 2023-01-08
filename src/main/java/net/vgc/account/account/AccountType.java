package net.vgc.account.account;

import net.vgc.language.TranslationKey;
import net.vgc.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum AccountType implements EnumRepresentable {
	
	USER("user", 0), GUEST("guest", 1), TEST("test", 2), UNKNOWN("unknown", 3);
	
	private final String name;
	private final int id;
	
	AccountType(String name, int id) {
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
	public Enum<AccountType> getDefault() {
		return UNKNOWN;
	}
	
	public String getTranslation() {
		return TranslationKey.createAndGet("account.type." + this.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
