package net.vgc.language;

import com.google.common.collect.Lists;

public class TranslationKey {
	
	protected final String key;
	
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
		String translation = LanguageProvider.INSTANCE.getTranslation(language, this);
		for (int i = 0; i < Lists.newArrayList(objects).size(); i++) {
			String s = "%" + (i + 1) + "%";
			if (translation.contains(s)) {
				translation = translation.replace(s, Lists.newArrayList(objects).get(i).toString());
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
