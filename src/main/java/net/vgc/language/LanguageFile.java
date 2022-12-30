package net.vgc.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.vgc.data.codec.CodecConstructor;
import net.vgc.data.codec.CodecGetter;
import net.vgc.util.Util;

import java.util.List;
import java.util.Map;
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
		this.translations = translations;
	}
	
	public LanguageFile(List<Translation> translations, Language language) {
		this.translations = translations;
		this.language = language;
	}
	
	@CodecConstructor
	private LanguageFile(Map<String, String> languageTranslations) {
		this.translations = Util.mapToList(languageTranslations, Translation::new);
	}
	
	@CodecGetter
	private Map<String, String> getLanguageKeyMap() {
		return this.translations.stream().collect(Collectors.toMap(Translation::getKey, Translation::getValue));
	}
	
	public List<Translation> getLanguageKeys() {
		return this.translations;
	}
	
	public boolean isEmpty() {
		return this.translations.isEmpty();
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}
	
}
