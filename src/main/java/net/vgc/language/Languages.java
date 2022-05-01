package net.vgc.language;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

public class Languages {
	
	public static final List<Language> LANGUAGES = Lists.newArrayList();
	public static final Language EN_US = register(new Language("American English", "en_us"));
	public static final Language DE_DE = register(new Language("Deutsch", "de_de"));
	
	protected static Language register(Language language) {
		LANGUAGES.add(language);
		return language;
	}
	
	@Nullable
	public static Language fromName(String name) {
		for (Language language : LANGUAGES) {
			if (language.getName().equals(name)) {
				return language;
			}
		}
		return null;
	}
	
	@Nullable
	public static Language fromFileName(String fileName) {
		for (Language language : LANGUAGES) {
			if (language.getFileName().equals(fileName)) {
				return language;
			}
		}
		return null;
	}
	
}
