package net.vgc.data.tag;

import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.language.TranslationKey;

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
