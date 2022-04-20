package net.vgc.common.settings;

import javax.annotation.Nonnull;

public abstract interface SettingValueType<T> {
	
	@Nonnull
	T getValue(String string);
	
	String toString(T value);
	
}
