package net.luis.language;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
	
	public void load(Path resourceDirectory) {
		List<LanguageFile> languageFiles = Lists.newArrayList();
		for (Language language : Languages.LANGUAGES) {
			Path path = language.getPath(resourceDirectory);
			if (Files.exists(path)) {
				LanguageFile languageFile = this.loadLanguage(resourceDirectory, language);
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
	
	private LanguageFile loadLanguage(Path resourceDirectory, Language language) {
		Path path = language.getPath(resourceDirectory);
		if (Files.exists(path)) {
			Optional<Pair<LanguageFile, JsonElement>> optional = JsonOps.INSTANCE.withDecoder(LanguageFile.CODEC).apply(this.loadJsonElement(path)).result();
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
	
	@Nullable
	private JsonElement loadJsonElement(Path path) {
		try {
			if (!Files.exists(path)) {
				LOGGER.warn("Unable to load file {}, since it does not exists", path);
				return null;
			}
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			JsonElement element = JsonParser.parseReader(reader);
			reader.close();
			return element;
		} catch (IOException e) {
			LOGGER.error("Fail to load json element from file {}", path);
			throw new RuntimeException(e);
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
