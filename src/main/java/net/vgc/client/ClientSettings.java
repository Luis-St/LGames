package net.vgc.client;

import com.google.common.collect.Lists;

import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.common.settings.AbstractSettings;
import net.vgc.common.settings.Setting;
import net.vgc.common.settings.SettingValueTypes;
import net.vgc.language.Language;
import net.vgc.language.LanguageProvider;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;

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
