package net.luis.utility.data.tag;

import net.luis.language.TranslationKey;
import net.luis.utils.data.tag.tags.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class TagUtil {
	
	public static @NotNull CompoundTag writeTranslationKey(TranslationKey key) {
		CompoundTag tag = new CompoundTag();
		tag.putString("key", Objects.requireNonNull(key, "Key must not be null").key());
		return tag;
	}
	
	public static @NotNull TranslationKey readTranslationKey(CompoundTag tag) {
		return new TranslationKey(Objects.requireNonNull(tag, "Tag must not be null").getString("key"));
	}
}
