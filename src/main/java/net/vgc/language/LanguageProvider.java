package net.vgc.language;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;

import net.vgc.data.json.JsonHelper;

public class LanguageProvider {
	
	public static final LanguageProvider INSTANCE = new LanguageProvider();
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected List<LanguageFile> loadLanguageFiles;
	protected Language currentLanguage = Languages.DE_DE;
	
	public void load() {
		List<LanguageFile> languageFiles = Lists.newArrayList();
		for (Language language : Languages.LANGUAGES) {
			Path path = language.getPath();
			if (Files.exists(path)) {
				LanguageFile languageFile = this.loadLanguage(language);
				if (languageFile != null) {
					if (!languageFile.isEmpty()) {
						languageFiles.add(languageFile);
						LOGGER.debug("Load language {}", language.getName());
					} else {
						LOGGER.warn("Fail to load language {}", language.getName());
					}
				}
			} else if (language == Languages.EN_US) {
				LOGGER.warn("Fail to load language {}, since language file not does not exists", language.getName());
			} else {
				LOGGER.info("Fail to load language {}", language.getName());
			}
		}
		if (languageFiles.isEmpty()) {
			LOGGER.warn("Unable to load languages");
		}
		this.loadLanguageFiles = languageFiles;
	}
	
	protected LanguageFile loadLanguage(Language language) {
		Path path = language.getPath();
		if (Files.exists(path)) {
			Optional<Pair<LanguageFile, JsonElement>> optional = JsonOps.INSTANCE.withDecoder(LanguageFile.CODEC).apply(JsonHelper.load(path)).result();
			if (optional.isPresent()) {
				return optional.get().getFirst();
			} else {
				LOGGER.warn("Fail to decode language file {} for language {}", path, language.getName());
				return null;
			}
		} else {
			LOGGER.warn("Fail to load language file {} for language {}, since it does not exists", path, language.getName());
			return null;
		}
	}
	
	public Language getCurrentLanguage() {
		return this.currentLanguage;
	}
	
	public void setCurrentLanguage(Language currentLanguage) {
		this.currentLanguage = currentLanguage;
	}
	
	public boolean isLanguageLoad(Language language) {
		return this.loadLanguageFiles.stream().map(LanguageFile::getLanguage).collect(Collectors.toList()).contains(language);
	}
	
	@Nullable
	public LanguageFile getFileForLanguage(Language language) {
		for (LanguageFile languageFile : this.loadLanguageFiles) {
			if (languageFile.getLanguage().equals(language)) {
				return languageFile;
			}
		}
		LOGGER.warn("Fail to get language file for language {}", language);
		return null;
	}
	
	public String getTranslation(TranslationKey key) {
		if (this.isLanguageLoad(this.currentLanguage)) {
			LanguageFile languageFile = this.getFileForLanguage(this.currentLanguage);
			if (languageFile != null) {
				for (Translation translation : languageFile.getLanguageKeys()) {
					if (translation.getKey().equals(key.getKey())) {
						return translation.getValue();
					}
				}
				LOGGER.warn("Fail to get translation for key {} in language {}, since the translation does not exists in the language file", key.getKey(), this.currentLanguage);
				return key.getKey();
			}
			LOGGER.warn("Fail to get translation for key {} in language {}, since language file is missing", key.getKey(), this.currentLanguage);
			return key.getKey();
		}
		LOGGER.warn("Fail to get translation for key {} in language {}, since the language is not load", key.getKey(), this.currentLanguage);
		return key.getKey();
	}
	
}
