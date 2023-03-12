package net.luis.language;

import joptsimple.ValueConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class LanguageConverter implements ValueConverter<Language> {
	@Override
	public @Nullable Language convert(@NotNull String value) {
		Language language = Languages.fromFileName(value);
		if (language == null) {
			language = Languages.fromName(value);
		}
		return language;
	}
	
	@Override
	public @NotNull Class<Language> valueType() {
		return Language.class;
	}
	
	@Override
	public @Nullable String valuePattern() {
		return null;
	}
}
