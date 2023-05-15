package net.luis.language;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record TranslationKey(@NotNull String key) {
	
	private static final Logger LOGGER = LogManager.getLogger(TranslationKey.class);
	
	public static @NotNull String createAndGet(@NotNull String key, @NotNull Object... objects) {
		return new TranslationKey(key).getValue(objects);
	}
	
	public static @NotNull String createAndGet(@NotNull Language language, @NotNull String key, @NotNull Object... objects) {
		return new TranslationKey(key).getValue(language, objects);
	}
	
	public @NotNull String getValue(@NotNull Object... objects) {
		return this.getValue(LanguageProvider.INSTANCE.getCurrentLanguage(), objects);
	}
	
	public @NotNull String getValue(@NotNull Language language, @NotNull Object... objects) {
		List<Object> objectList = Lists.newArrayList(objects);
		String translation = LanguageProvider.INSTANCE.getTranslation(language, this);
		for (int i = 0; i < objectList.size(); i++) {
			String s = "%" + (i + 1) + "%";
			if (translation.contains(s)) {
				Object object = objectList.get(i);
				if (object != null) {
					translation = translation.replace(s, object.toString());
				} else {
					translation = translation.replace(s, "null");
					LOGGER.warn("Translation key {} requires object insertion for insertion key {}, but there is no object for this key", this.key, s);
				}
			}
		}
		return translation;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof TranslationKey that)) return false;
		
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public @NotNull String toString() {
		return this.key;
	}
}
