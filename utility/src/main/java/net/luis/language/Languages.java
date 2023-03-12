package net.luis.language;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class Languages {
	
	public static final List<Language> LANGUAGES = Lists.newArrayList();
	public static final Language EN_US = register(new Language("American English", "en_us"));
	public static final Language DE_DE = register(new Language("Deutsch", "de_de"));
	
	private static @NotNull Language register(@NotNull Language language) {
		LANGUAGES.add(language);
		return language;
	}
	
	public static @Nullable Language fromName(@NotNull String name) {
		for (Language language : LANGUAGES) {
			if (language.name().equals(name)) {
				return language;
			}
		}
		return null;
	}
	
	public static @Nullable Language fromFileName(@NotNull String fileName) {
		for (Language language : LANGUAGES) {
			if (language.fileName().equals(fileName)) {
				return language;
			}
		}
		return null;
	}
	
}
