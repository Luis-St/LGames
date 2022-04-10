package net.vgc.language;

import java.nio.file.Path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.vgc.client.Client;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;
import net.vgc.server.account.AccountServer;
import net.vgc.util.annotation.CodecConstructor;
import net.vgc.util.annotation.CodecGetter;

public class Language {
	
	public static final Codec<Language> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.STRING.fieldOf("name").forGetter(Language::getName), Codec.STRING.fieldOf("file_name").forGetter(Language::getFileName)).apply(instance, Language::new);
	});
	
	protected final String name;
	protected final String fileName;
	
	@CodecConstructor
	public Language(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}
	
	@CodecGetter
	public String getName() {
		return this.name;
	}
	
	@CodecGetter
	public String getFileName() {
		return this.fileName;
	}
	
	public Path getPath() {
		if (NetworkSide.CLIENT.isOn()) {
			return Client.getInstance().getResourceDirectory().resolve("assets/lang/" + this.fileName + ".json");
		} else if (NetworkSide.SERVER.isOn()) {
			throw new UnsupportedOperationException("Not implemeted yet"); // TODO: add server side path
		} else if (NetworkSide.ACCOUNT_SERVER.isOn()) {
			return AccountServer.getInstance().getResourceDirectory().resolve("assets/lang/" + this.fileName + ".json");
		}
		throw new IllegalStateException("Unknown network side: " + Network.INSTANCE.getNetworkSide());
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
