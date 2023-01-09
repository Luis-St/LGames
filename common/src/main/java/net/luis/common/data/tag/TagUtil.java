package net.luis.common.data.tag;

import net.luis.language.TranslationKey;
import net.luis.utils.data.tag.tags.CompoundTag;

/**
 *
 * @author Luis-st
 *
 */

public class TagUtil {
	
	public static CompoundTag writeTranslationKey(TranslationKey key) {
		CompoundTag tag = new CompoundTag();
		tag.putString("key", key.key());
		return tag;
	}
	
	public static TranslationKey readTranslationKey(CompoundTag tag) {
		return new TranslationKey(tag.getString("key"));
	}
	
}
