package net.luis.utility.settings;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface SettingValueType<T> {
	
	@NotNull
	T getValue(String string);
	
	String toString(T value);
	
}
