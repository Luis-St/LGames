package net.vgc.common.settings;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.data.serialization.Serializable;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.data.tag.tags.StringTag;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;
import net.vgc.util.exception.InvalidValueException;

public class Setting<T> implements Serializable {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final List<BiConsumer<T, T>> listeners = Lists.newArrayList();
	private final TranslationKey name;
	private final TranslationKey description;
	private final SettingValueType<T> valueType;
	private final T defaultValue;
	private final List<T> possibleValues;
	private T value;
	
	@SafeVarargs
	public Setting(TranslationKey name, TranslationKey description, SettingValueType<T> valueType, T defaultValue, T... possibleValues) {
		this(name, description, valueType, defaultValue, Lists.newArrayList(possibleValues));
	}
	
	public Setting(TranslationKey name, TranslationKey description, SettingValueType<T> valueType, T defaultValue, List<T> possibleValues) {
		this.name = name;
		this.description = description;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
	}
	
	@SuppressWarnings("unchecked")
	public Setting(CompoundTag tag) {
		this.name = TagUtil.readTranslationKey(tag.getCompound("name_key"));
		this.description = TagUtil.readTranslationKey(tag.getCompound("description_key"));
		int valueTypeId = tag.getInt("value_type");
		this.valueType = (SettingValueType<T>) SettingValueTypes.fromId(valueTypeId);
		Objects.requireNonNull(this.valueType, "Fail to load SettingValueType from tag, since SettingValueType with id " + valueTypeId + " is not registered or does not exists");
		this.defaultValue = this.valueType.getValue(tag.getString("default_value"));
		this.possibleValues = TagUtil.readList(tag.getList("possible_values", Tag.STRING_TAG), (stringTag) -> {
			return Setting.this.valueType.getValue(((StringTag) stringTag).getAsString());
		});
		if (tag.contains("value")) {
			this.value = this.valueType.getValue(tag.getString("value"));
			LOGGER.info("Load value and set to {}", this.value);
		}
	}
	
	public void addListener(BiConsumer<T, T> listener) {
		this.listeners.add(listener);
	}
	
	protected void triggerListeners(T oldValue, T newValue) {
		if (oldValue == null) {
			LOGGER.debug("Update value of setting {} from default {} to {}", this.name.getValue(Languages.EN_US), this.defaultValue, newValue);
		} else {
			LOGGER.debug("Update value of setting {} from {} to {}", this.name.getValue(Languages.EN_US), oldValue, newValue);
		}
		this.listeners.forEach((listener) -> {
			listener.accept(oldValue, newValue);
		});
	}
	
	public String getName() {
		return this.name.getValue();
	}
	
	public String getDescription() {
		return this.description.getValue();
	}
	
	public T getDefaultValue() {
		return this.defaultValue;
	}
	
	public List<T> getPossibleValues() {
		return this.possibleValues;
	}
	
	public T getValue() {
		if (this.value != null) {
			return this.value;
		}
		return this.defaultValue;
	}
	
	public void setValue(T value) {
		this.triggerListeners(this.value, value);
		this.value = value;
	}
	
	public void setValue(String value) {
		this.triggerListeners(this.value, this.valueType.getValue(value));
		this.value = this.valueType.getValue(value);
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.put("name_key", TagUtil.writeTranslationKey(this.name));
		tag.put("description_key", TagUtil.writeTranslationKey(this.description));
		int id = SettingValueTypes.getId(this.valueType);
		if (id == -1) {
			LOGGER.error("Fail to serialize SettingValueType to Tag, since id {} is not registered", id);
			throw new InvalidValueException("Fail to get SettingValueType for id " + id);
		}
		tag.putInt("value_type", id);
		tag.putString("default_value", this.valueType.toString(this.defaultValue));
		tag.putList("possible_values", TagUtil.writeList(this.possibleValues, (value) -> {
			return StringTag.valueOf(this.valueType.toString(value));
		}));
		if (this.getValue() != null) {
			LOGGER.info("Save value {}", this.value);
			tag.putString("value", this.valueType.toString(this.getValue()));
		}
		return tag;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Setting{");
		builder.append("name=").append(this.name).append(",");
		builder.append("description=").append(this.description).append(",");
		builder.append("value_type=").append(this.valueType).append(",");
		builder.append("default_value=").append(this.defaultValue).append(",");
		builder.append("possible_values=").append(this.possibleValues).append(",");
		builder.append("value=").append(this.value).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Setting<?> setting) {
			return this.name.equals(setting.name) && this.description.equals(setting.description) && this.valueType == setting.valueType && this.defaultValue.equals(setting.defaultValue) && this.possibleValues.equals(setting.possibleValues);
		}
		return false;
	}
	
}
