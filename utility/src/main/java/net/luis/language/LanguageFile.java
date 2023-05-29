package net.luis.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class LanguageFile {
	
	public static final Codec<LanguageFile> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("keys").forGetter(LanguageFile::getLanguageKeyMap)).apply(instance, LanguageFile::new);
	});
	
	private final List<Translation> translations;
	private Language language;
	
	public LanguageFile(List<Translation> translations) {
		this(translations, null);
	}
	
	public LanguageFile(List<Translation> translations, @Nullable Language language) {
		this.translations = Objects.requireNonNull(translations, "Translations must not be null");
		this.setLanguage(language);
	}
	
	private LanguageFile(Map<String, String> languageTranslations) {
		this.translations = Utils.mapToList(languageTranslations, Translation::new);
	}
	
	private @NotNull Map<String, String> getLanguageKeyMap() {
		return this.translations.stream().collect(Collectors.toMap(Translation::key, Translation::value));
	}
	
	public @NotNull List<Translation> getLanguageKeys() {
		return this.translations;
	}
	
	public boolean isEmpty() {
		return this.translations.isEmpty();
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public void setLanguage(@Nullable Language language) {
		this.language = language;
	}
	
	//region Object overrides
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof LanguageFile that)) return false;
		
		if (!this.translations.equals(that.translations)) return false;
		return Objects.equals(this.language, that.language);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.translations, this.language);
	}
	//endregion
}
