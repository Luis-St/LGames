package net.luis.game.resources;

import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record ResourceManager(@NotNull Path gameDirectory, @NotNull Path resourceDirectory) {
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof ResourceManager that)) return false;
		
		if (!this.gameDirectory.equals(that.gameDirectory)) return false;
		return this.resourceDirectory.equals(that.resourceDirectory);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.gameDirectory, this.resourceDirectory);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this);
	}
}
