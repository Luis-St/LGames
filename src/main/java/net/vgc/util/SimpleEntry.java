package net.vgc.util;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.vgc.Main;

public class SimpleEntry<K, V> implements Map.Entry<K, V> {
	
	private final K key;
	private V value;
	private boolean muted;
	
	public SimpleEntry(K key) {
		this(key, null);
	}
	
	public SimpleEntry(K key, V value) {
		this(key, value, false);
	}
	
	public SimpleEntry(K key, V value, boolean muted) {
		this.key = key;
		this.value = value;
		this.muted = muted;
	}
	
	@Override
	public K getKey() {
		return this.key;
	}
	
	@Nullable
	@Override
	public V getValue() {
		return this.value;
	}
	
	@Nullable
	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		if (!this.muted) {
			this.value = value;
		} else {
			Main.LOGGER.info("Unable to set value of entry to {}, since the entry is muted", value);
		}
		return oldValue;
	}
	
	public boolean isMuted() {
		return this.muted;
	}
	
	public void setMuted() {
		this.muted = true;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof SimpleEntry<?, ?> entry) {
			if (!this.key.equals(entry.getKey())) {
				return false;
			} else if (!this.value.equals(entry.getValue())) {
				return false;
			} else {
				return this.muted == entry.isMuted();
			}
		}
		return false;
	}
	
}
