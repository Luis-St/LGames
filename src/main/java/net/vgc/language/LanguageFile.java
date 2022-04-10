package net.vgc.language;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.vgc.util.Util;
import net.vgc.util.annotation.CodecConstructor;
import net.vgc.util.annotation.CodecGetter;

public class LanguageFile {
	
	public static final Codec<LanguageFile> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Language.CODEC.fieldOf("language").forGetter(LanguageFile::getLanguage), Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("keys").forGetter(LanguageFile::getLanguageKeyMap)).apply(instance, LanguageFile::new);
	});
	
	protected final Language language;
	protected final List<Translation> translations;
	
	public LanguageFile(Language language, Translation... translations) {
		this(language, Lists.newArrayList(translations));
	}
	
	public LanguageFile(Language language, List<Translation> translations) {
		this.language = language;
		this.translations = translations;
	}
	
	@CodecConstructor
	private LanguageFile(Language language, Map<String, String> languageTranslations) {
		this.language = language;
		this.translations = Util.mapList(languageTranslations, Translation::new);
	}
	
	@CodecGetter
	public Language getLanguage() {
		return this.language;
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
	
}
