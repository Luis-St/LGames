package net.vgc.common.settings;

import com.google.common.collect.Lists;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.StringTag;
import net.vgc.data.tag.TagUtil;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;
import net.vgc.util.exception.InvalidValueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 *
 * @author Luis-st
 *
 */

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
		this.name = TagUtil.readTranslationKey(tag.getCompound("NameKey"));
		this.description = TagUtil.readTranslationKey(tag.getCompound("DescriptionKey"));
		int valueTypeId = tag.getInt("ValueType");
		this.valueType = (SettingValueType<T>) SettingValueTypes.fromId(valueTypeId);
		Objects.requireNonNull(this.valueType, "Fail to load SettingValueType from tag, since SettingValueType with id " + valueTypeId + " is not registered or does not exists");
		this.defaultValue = this.valueType.getValue(tag.getString("DefaultValue"));
		this.possibleValues = TagUtil.readList(tag.getList("PossibleValues", Tag.STRING_TAG), (stringTag) -> {
			return Setting.this.valueType.getValue(stringTag.getAsString());
		});
		if (tag.contains("Value")) {
			this.value = this.valueType.getValue(tag.getString("Value"));
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
		tag.put("NameKey", TagUtil.writeTranslationKey(this.name));
		tag.put("DescriptionKey", TagUtil.writeTranslationKey(this.description));
		int id = SettingValueTypes.getId(this.valueType);
		if (id == -1) {
			LOGGER.error("Fail to serialize SettingValueType to Tag, since id {} is not registered", id);
			throw new InvalidValueException("Fail to get SettingValueType for id " + id);
		}
		tag.putInt("ValueType", id);
		tag.putString("DefaultValue", this.valueType.toString(this.defaultValue));
		tag.putList("PossibleValues", TagUtil.writeList(this.possibleValues, (value) -> {
			return StringTag.valueOf(this.valueType.toString(value));
		}));
		if (this.getValue() != null) {
			LOGGER.info("Save value {}", this.value);
			tag.putString("Value", this.valueType.toString(this.getValue()));
		}
		return tag;
	}
	
	@Override
	public String toString() {
		String builder = "Setting{" + "name=" + this.name + "," +
				"description=" + this.description + "," +
				"value_type=" + this.valueType + "," +
				"default_value=" + this.defaultValue + "," +
				"possible_values=" + this.possibleValues + "," +
				"value=" + this.value + "}";
		return builder;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Setting<?> setting) {
			return this.name.equals(setting.name) && this.description.equals(setting.description) && this.valueType == setting.valueType && this.defaultValue.equals(setting.defaultValue) && this.possibleValues.equals(setting.possibleValues);
		}
		return false;
	}
	
}
