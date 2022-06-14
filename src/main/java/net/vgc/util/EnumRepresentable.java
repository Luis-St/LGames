package net.vgc.util;

import javax.annotation.Nullable;

import net.vgc.Main;

public interface EnumRepresentable {
	
	String getName();
	
	int getId();
	
	@Nullable
	default Enum<?> getDefault() {
		return null;
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & EnumRepresentable> T fromName(Class<T> clazz, String name) {
		if (clazz.isEnum()) {
			T[] values = clazz.getEnumConstants();
			for (T value : values) {
				if (value.getName().equals(name)) {
					return value;
				}
			}
			if (values.length != 0) {
				return (T) values[0].getDefault();
			}
		} else {
			Main.LOGGER.warn("Fail to get enum value from name {}, since the class {} is not a enum", name, clazz.getSimpleName());
		}
		return null;
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends EnumRepresentable> T fromId(Class<T> clazz, int id) {
		if (clazz.isEnum()) {
			T[] values = clazz.getEnumConstants();
			for (T value : values) {
				if (value.getId() == id) {
					return value;
				}
			}
			if (values.length != 0) {
				return (T) values[0].getDefault();
			}
		} else {
			Main.LOGGER.warn("Fail to get enum value from id {}, since the class {} is not a enum", id, clazz.getSimpleName());
		}
		return null;
	}
	
}
