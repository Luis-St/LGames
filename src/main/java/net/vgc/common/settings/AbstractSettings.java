package net.vgc.common.settings;

import java.util.List;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagUtil;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.data.serialization.Serializable;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractSettings implements Serializable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private final List<Setting<?>> settings;
	
	protected AbstractSettings(CompoundTag tag) {
		this(TagUtil.readList(tag.getList("settings", Tag.COMPOUND_TAG), (settingTag) -> {
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
		tag.putList("settings", TagUtil.writeList(this.settings, (setting) -> {
			return setting.serialize();
		}));
		return tag;
	}
	
}
