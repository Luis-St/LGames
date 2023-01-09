package net.luis.data.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Luis-st
 *
 */

public class JsonHelper {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Nullable
	public static JsonElement load(Path path) {
		try {
			if (!Files.exists(path)) {
				LOGGER.warn("Unable to load file {}, since it does not exists", path);
				return null;
			}
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			JsonElement element = JsonParser.parseReader(reader);
			reader.close();
			return element;
		} catch (IOException e) {
			LOGGER.error("Fail to load json element from file {}", path);
			throw new RuntimeException(e);
		}
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
			LOGGER.error("Fail to save json element to file {}", path);
			throw new RuntimeException(e);
		}
	}
	
}
