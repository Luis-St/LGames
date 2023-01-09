package net.luis.language;

import net.luis.utils.util.ToString;
import net.luis.network.Network;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record Language(String name, String fileName) {
	
	public Path getPath() {
		return Network.INSTANCE.getResourceDirectory().resolve("lang/" + this.fileName + ".json");
	}
	
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
		return ToString.toString(this, "fileName");
	}
	
}
