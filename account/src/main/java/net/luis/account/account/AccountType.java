package net.luis.account.account;

import net.luis.language.TranslationKey;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public enum AccountType {
	
	USER("user"), GUEST("guest"), TEST("test"), UNKNOWN("unknown");
	
	private final String name;
	
	AccountType(@NotNull String name) {
		this.name = name;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull String getTranslation() {
		return TranslationKey.createAndGet("account.type." + this.name);
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
	
}
