package net.luis.settings;

import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagUtils;
import net.luis.utils.data.tag.tags.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractSettings implements Serializable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private final List<Setting<?>> settings;
	
	protected AbstractSettings(CompoundTag tag) {
		this(TagUtils.readList(tag.getList("Settings", Tag.COMPOUND_TAG), (settingTag) -> {
			return new Setting<>((CompoundTag) settingTag);
		}));
	}
	
	protected AbstractSettings(List<Setting<?>> settings) {
		this.settings = settings;
		this.init();
	}
	
	protected abstract void init();
	
	protected final void register(Setting<?> setting) {
		if (!this.settings.contains(setting)) {
			this.settings.add(setting);
		}
	}
	
	protected final <T> void addListener(Setting<T> setting, BiConsumer<T, T> listener) {
		if (this.settings.contains(setting)) {
			setting.addListener(listener);
		}
	}
	
	public List<Setting<?>> getSettings() {
		return this.settings;
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putList("Settings", TagUtils.writeList(this.settings, Setting::serialize));
		return tag;
	}
	
}
