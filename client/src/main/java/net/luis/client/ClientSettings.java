package net.luis.client;

import com.google.common.collect.Lists;
import net.luis.language.Language;
import net.luis.language.LanguageProvider;
import net.luis.language.Languages;
import net.luis.language.TranslationKey;
import net.luis.utility.settings.AbstractSettings;
import net.luis.utility.settings.Setting;
import net.luis.utility.settings.SettingValueTypes;
import net.luis.utils.data.tag.tags.CompoundTag;

/**
 *
 * @author Luis-st
 *
 */

public class ClientSettings extends AbstractSettings {
	
	public static final Setting<Language> LANGUAGE = new Setting<>(new TranslationKey("settings.language.name"), new TranslationKey("settings.language.description"), SettingValueTypes.LANGUAGE, Languages.EN_US, Languages.LANGUAGES);
	
	public ClientSettings() {
		super(Lists.newArrayList());
	}
	
	public ClientSettings(CompoundTag tag) {
		super(tag);
	}
	
	@Override
	public void init() {
		this.register(LANGUAGE);
		this.addListener(LANGUAGE, (oldValue, newValue) -> {
			if (newValue != null) {
				LanguageProvider.INSTANCE.setCurrentLanguage(newValue);
			}
		});
	}
	
}
