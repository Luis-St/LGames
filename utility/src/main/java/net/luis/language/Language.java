package net.luis.language;

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

public record Language(@NotNull String name, @NotNull String fileName) {
	
	public @NotNull Path getPath(@NotNull Path resourceDirectory) {
		return resourceDirectory.resolve("lang/" + this.fileName + ".json");
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof Language language)) return false;
		
		if (!this.name.equals(language.name)) return false;
		return this.fileName.equals(language.fileName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.fileName);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this, "fileName");
	}
	
}
