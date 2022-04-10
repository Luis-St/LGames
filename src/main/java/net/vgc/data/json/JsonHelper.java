package net.vgc.data.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonHelper {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	@Nullable
	public static JsonElement load(Path path) {
		try {
			if (!Files.exists(path)) {
				LOGGER.warn("Unbale to load file {}, since it does not exists", path);
				return null;
			}
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			JsonElement element = JsonParser.parseReader(reader);
			reader.close();
			return element;
		} catch (IOException e) {
			LOGGER.warn("Fail to load file {}, since: {}", path, e.getMessage());
		}
		return null;
	}
	
	public static void save(Gson gson, JsonElement jsonElement, Path path) {
		try {
			String element = gson.toJson(jsonElement);
			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			writer.write(element);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOGGER.warn("Fail to save file {}, since: {}", path, e.getMessage());
		}
	}
	
}
