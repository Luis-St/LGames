package net.luis.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record Translation(@NotNull String key, @NotNull String value) {
	
	@Override
	public boolean equals(@Nullable Object o) {
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
	public @NotNull String toString() {
		return this.key + ":" + this.value;
	}
	
	
}
