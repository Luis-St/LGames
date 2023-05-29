package net.luis.language;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record Language(String name, String fileName) {
	
	public Language {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(fileName, "File name must not be null");
	}
	
	public @NotNull Path getPath(@NotNull Path resourceDirectory) {
		return resourceDirectory.resolve("lang/" + this.fileName + ".json");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
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
	public String toString() {
		return "Language{name='" + this.name + '\'' + ", fileName='" + this.fileName + '\'' + "}";
	}
	//endregion
}
