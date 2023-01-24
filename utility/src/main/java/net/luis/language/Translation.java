package net.luis.language;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record Translation(String key, String value) {
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Translation that)) return false;
		
		if (!this.key.equals(that.key)) return false;
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
	
	@Override
	public String toString() {
		return this.key + ":" + this.value;
	}
	
	
}
