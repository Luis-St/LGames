package net.vgc.language;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.vgc.data.json.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Luis-st
 *
 */

public class LanguageProvider {
	
	public static final LanguageProvider INSTANCE = new LanguageProvider();
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private List<LanguageFile> loadLanguageFiles;
	private Language currentLanguage = Languages.EN_US;
	
	public void load() {
		List<LanguageFile> languageFiles = Lists.newArrayList();
		for (Language language : Languages.LANGUAGES) {
			Path path = language.getPath();
			if (Files.exists(path)) {
				LanguageFile languageFile = this.loadLanguage(language);
				if (languageFile != null) {
					if (!languageFile.isEmpty()) {
						languageFile.setLanguage(language);
						languageFiles.add(languageFile);
						LOGGER.debug("Load language {}", language.name());
					} else {
						LOGGER.warn("Fail to load language {}", language.name());
					}
				}
			} else if (language == Languages.EN_US) {
				LOGGER.warn("Fail to load language {}, since language file not does not exists", language.name());
			} else {
				LOGGER.info("Fail to load language {}", language.name());
			}
		}
		if (languageFiles.isEmpty()) {
			LOGGER.warn("Unable to load languages");
		}
		this.loadLanguageFiles = languageFiles;
	}
	
	private LanguageFile loadLanguage(Language language) {
		Path path = language.getPath();
		if (Files.exists(path)) {
			Optional<Pair<LanguageFile, JsonElement>> optional = JsonOps.INSTANCE.withDecoder(LanguageFile.CODEC).apply(JsonHelper.load(path)).result();
			if (optional.isPresent()) {
				return optional.get().getFirst();
			} else {
				LOGGER.warn("Fail to decode language file {} for language {}", path, language.name());
				return null;
			}
		} else {
			LOGGER.warn("Fail to load language file {} for language {}, since it does not exists", path, language.name());
			return null;
		}
	}
	
	public Language getCurrentLanguage() {
		return this.currentLanguage;
	}
	
	public void setCurrentLanguage(Language currentLanguage) {
		LOGGER.debug("Change language from {} to {}", this.currentLanguage, currentLanguage);
		this.currentLanguage = currentLanguage;
	}
	
	public boolean isLanguageLoad(Language language) {
		return this.loadLanguageFiles.stream().map(LanguageFile::getLanguage).toList().contains(language);
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
	
	public String getTranslation(Language language, TranslationKey key) {
		if (this.isLanguageLoad(language)) {
			LanguageFile languageFile = this.getFileForLanguage(language);
			if (languageFile != null) {
				for (Translation translation : languageFile.getLanguageKeys()) {
					if (translation.key().equals(key.key())) {
						return translation.value();
					}
				}
				LOGGER.warn("Fail to get translation for key {} in language {}, since the translation does not exists in the language file", key.key(), language);
				return key.key();
			}
			LOGGER.warn("Fail to get translation for key {} in language {}, since language file is missing", key.key(), language);
			return key.key();
		}
		LOGGER.warn("Fail to get translation for key {} in language {}, since the language is not load", key.key(), language);
		return key.key();
	}
	
	public String getTranslation(TranslationKey key) {
		return this.getTranslation(this.currentLanguage, key);
	}
	
}
