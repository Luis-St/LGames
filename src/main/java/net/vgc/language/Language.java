package net.vgc.language;

import net.vgc.network.Network;

import java.nio.file.Path;

/**
 *
 * @author Luis-st
 *
 */

public class Language {
	
	private final String name;
	private final String fileName;
	
	public Language(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public Path getPath() {
		return Network.INSTANCE.getResourceDirectory().resolve("lang/" + this.fileName + ".json");
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Language language) {
			return this.name.equals(language.name) && this.fileName.equals(language.fileName);
		}
		return false;
	}
	
}
