package net.vgc.util;

import java.util.Objects;

public class MutableObject<T> {
	
	protected T value;
	
	public MutableObject() {
		
	}
	
	public MutableObject(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "MutableObject{value=" + this.value + "}";
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof MutableObject<?> mutableObject) {
			return Objects.equals(this.value, mutableObject.value);
		}
		return false;
	}
	
}
