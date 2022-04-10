package net.vgc.language;

import java.util.List;

import com.google.common.collect.Lists;

public class Languages {
	
	public static final List<Language> LANGUAGES = Lists.newArrayList();
	public static final Language EN_US = register(new Language("American English", "en_us"));
//	public static final Language EN_EN = register(new Language("British English", "en_en"));
	public static final Language DE_DE = register(new Language("Deutsch", "de_de"));
	
	protected static Language register(Language language) {
		LANGUAGES.add(language);
		return language;
	}
	
}
