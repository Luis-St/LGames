package net.vgc.language;

import java.util.List;

import com.google.common.collect.Lists;

public class TranslationKey {
	
	protected final String key;
	protected final List<Object> objects;
	
	public TranslationKey(String key, Object... objects) {
		this.key = key;
		this.objects = Lists.newArrayList(objects);
	}
	
	public static String createAndGet(String key, Object... objects) {
		return new TranslationKey(key, objects).getValue();
	}
	
	public static String createAndGet(Language language, String key, Object... objects) {
		return new TranslationKey(key, objects).getValue(language);
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		String translation = LanguageProvider.INSTANCE.getTranslation(this);
		for (int i = 0; i < this.objects.size(); i++) {
			String s = "%" + (i + 1) + "%";
			if (translation.contains(s)) {
				translation = translation.replace(s, this.objects.get(i).toString());
			}
		}
		return translation;
	}
	
	public String getValue(Language language) {
		String translation = LanguageProvider.INSTANCE.getTranslation(language, this);
		for (int i = 0; i < this.objects.size(); i++) {
			String s = "%" + (i + 1) + "%";
			if (translation.contains(s)) {
				translation = translation.replace(s, this.objects.get(i).toString());
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
