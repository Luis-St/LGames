package net.luis.game.application;

import javafx.animation.Timeline;
import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.Constants;
import net.luis.game.resources.ResourceManager;
import net.luis.language.Language;
import net.luis.language.LanguageConverter;
import net.luis.language.LanguageProvider;
import net.luis.language.Languages;
import net.luis.utility.Tickable;
import net.luis.utility.Util;
import net.luis.utils.util.DefaultExceptionHandler;
import net.luis.utils.util.reflection.ClassPathUtils;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class InternalApplication extends javafx.application.Application {
	
	private static final Logger LOGGER = LogManager.getLogger(InternalApplication.class);
	
	private FxApplication application;
	private Timeline ticker;
	
	@Override
	public void start(@NotNull Stage stage) throws Exception {
		this.start(stage, this.getParameters().getRaw().toArray(String[]::new));
	}
	
	private void initThread(@NotNull String name) {
		Thread currentThread = Thread.currentThread();
		currentThread.setName(name.toLowerCase());
		currentThread.setUncaughtExceptionHandler(new DefaultExceptionHandler());
	}
	
	private void start(@NotNull Stage stage, @NotNull String[] args) throws Exception {
		this.initThread("internal");
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<String> launchTarget = parser.accepts("launchTarget").withRequiredArg().ofType(String.class);
		OptionSpec<Boolean> debugMode = parser.accepts("debugMode").withRequiredArg().ofType(Boolean.class).defaultsTo(false);
		OptionSpec<Boolean> devMode = parser.accepts("devMode").withRequiredArg().ofType(Boolean.class).defaultsTo(false);
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File(System.getProperty("user.home")).toPath().resolve("Desktop/run").toFile());
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<Language> language = parser.accepts("language").withRequiredArg().ofType(Language.class).withValuesConvertedBy(new LanguageConverter()).defaultsTo(Languages.EN_US);
		OptionSet set = parser.parse(args);
		if (!set.has(launchTarget)) {
			throw new IllegalArgumentException("Missing launch target");
		} else {
			Class<?> target = this.getLaunchTarget(set.valueOf(launchTarget));
			Constants.DEBUG_MODE = set.valueOf(debugMode);
			if (Constants.DEBUG_MODE) LOGGER.debug("Debug mode enabled");
			Constants.DEV_MODE = set.valueOf(devMode);
			if (Constants.DEV_MODE) LOGGER.debug("Dev mode enabled");
			Path gameDirectory = set.valueOf(gameDir).toPath();
			if (!Files.isDirectory(gameDirectory)) {
				gameDirectory = gameDirectory.getParent();
				LOGGER.warn("Game directory is not a directory, using parent directory");
			}
			if (Files.exists(gameDirectory)) {
				Files.createDirectories(gameDirectory);
				LOGGER.debug("Created game directory");
			}
			LOGGER.info("Launch from directory: {}", gameDirectory);
			Path resourceDirectory = set.has(resourceDir) ? set.valueOf(resourceDir).toPath() : gameDirectory.resolve("assets");
			if (!Files.isDirectory(resourceDirectory)) {
				resourceDirectory = resourceDirectory.getParent();
				LOGGER.warn("Resource directory is not a directory, using parent directory");
			}
			if (Files.exists(resourceDirectory)) {
				Files.createDirectories(resourceDirectory);
				LOGGER.debug("Created resource directory");
			}
			LanguageProvider.INSTANCE.load(resourceDirectory);
			LanguageProvider.INSTANCE.setCurrentLanguage(set.valueOf(language));
			try {
				ApplicationType type = target.getAnnotation(Application.class).value();
				FxApplication application = this.createApplication(target, type, new ResourceManager(gameDirectory, resourceDirectory), stage);
				LOGGER.info("Created application: {}", type.getName());
				this.initThread(type.getShortName());
				application.load(args);
				LOGGER.info("Loaded application: {}", type.getName());
				if (application instanceof Tickable tickable) {
					this.startTickable(application, type, tickable);
					LOGGER.info("Started tickable application: {}", type.getName());
				}
				application.run();
				application.getStage().show();
				LOGGER.info("Launched application: {}", type.getName());
				this.application = application;
			} catch (Exception e) {
				throw new RuntimeException("Failed to start application", e);
			}
		}
	}
	
	private @NotNull Class<?> getLaunchTarget(@NotNull String target) {
		List<Class<?>> targets = ClassPathUtils.getAnnotatedClasses(Application.class);
		for (Class<?> clazz : targets) {
			if (clazz.getAnnotation(Application.class).value().getShortName().equalsIgnoreCase(target)) {
				return clazz;
			}
		}
		throw new IllegalArgumentException("Could not find launch target: " + target);
	}
	
	private @NotNull FxApplication createApplication(@NotNull Class<?> target, @NotNull ApplicationType type, @NotNull ResourceManager resourceManager, @NotNull Stage stage) {
		ReflectionHelper.enableExceptionLogging();
		FxApplication application = (FxApplication) ReflectionHelper.newInstance(target, type, resourceManager, stage);
		ReflectionHelper.disableExceptionLogging();
		if (application != null) {
			return application;
		}
		throw new RuntimeException("Failed to create application");
	}
	
	private void startTickable(@NotNull FxApplication application, @NotNull ApplicationType type, @NotNull Tickable tickable) {
		this.ticker = Util.createTicker(StringUtils.capitalize(type.getShortName()) + "Ticker", () -> {
			tickable.tick();
			if (application.getScreen() instanceof Tickable screenTickable) {
				screenTickable.tick();
			}
		});
		this.ticker.play();
	}
	
	@Override
	public void stop() throws Exception {
		this.application.save();
		LOGGER.info("Saved application: {}", this.application.getType().getName());
		if (this.ticker != null) {
			this.ticker.stop();
		}
	}
	
}
