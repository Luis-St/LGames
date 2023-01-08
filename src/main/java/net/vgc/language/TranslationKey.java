package net.vgc.language;

import com.google.common.collect.Lists;
import net.vgc.Main;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record TranslationKey(String key) {
	
	public static String createAndGet(String key, Object... objects) {
		return new TranslationKey(key).getValue(objects);
	}
	
	public static String createAndGet(Language language, String key, Object... objects) {
		return new TranslationKey(key).getValue(language, objects);
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TranslationKey that)) return false;
		
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return this.key;
	}
	
	
}
