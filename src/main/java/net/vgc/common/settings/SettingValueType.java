package net.vgc.common.settings;

import org.jetbrains.annotations.NotNull;

public abstract interface SettingValueType<T> {
	
	@NotNull
	T getValue(String string);
	
	String toString(T value);
	
}
