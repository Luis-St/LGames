package net.luis.utility.data.tag;

import net.luis.language.TranslationKey;
import net.luis.utils.data.tag.tags.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class TagUtil {
	
	public static @NotNull CompoundTag writeTranslationKey(@NotNull TranslationKey key) {
		CompoundTag tag = new CompoundTag();
		tag.putString("key", key.key());
		return tag;
	}
	
	public static @NotNull TranslationKey readTranslationKey(@NotNull CompoundTag tag) {
		return new TranslationKey(tag.getString("key"));
	}
	
}
