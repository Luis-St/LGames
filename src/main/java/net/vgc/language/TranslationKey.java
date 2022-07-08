package net.vgc.language;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.Main;

public class TranslationKey {
	
	private final String key;
	
	public TranslationKey(String key) {
		this.key = key;
	}
	
	public static String createAndGet(String key, Object... objects) {
		return new TranslationKey(key).getValue(objects);
	}
	
	public static String createAndGet(Language language, String key, Object... objects) {
		return new TranslationKey(key).getValue(language, objects);
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue(Object... objects) {
		return this.getValue(LanguageProvider.INSTANCE.getCurrentLanguage(), objects);
	}
	
	public String getValue(Language language, Object... objects) {
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
					Main.LOGGER.warn("Translation key {} requires object insertion for insertion key {}, but there is no object for this key", this.key, s);
				}
			}
		}
		return translation;
	}
	
	@Override
	public String toString() {
		return this.key;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TranslationKey translationKey) {
			return this.key.equals(translationKey.key);
		}
		return false;
	}
	
}
