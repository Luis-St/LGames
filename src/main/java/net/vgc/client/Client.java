package net.vgc.client;

import java.io.File;
import java.nio.file.Path;
import java.util.Random;

import javax.annotation.Nullable;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.screen.Screen;
import net.vgc.client.window.ErrorWindow;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;
import net.vgc.util.Util;

public class Client extends GameApplication {
	
	protected static Client instance;
	
	protected final Timeline ticker = Util.createTicker("ClientTicker", () -> {
		Client.getInstance().tick();
	});
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Scene currentScene;
	protected Scene previousScene;
	protected boolean instantLoading;
	protected Random rng;
	
	@Nullable
	public static Client getInstance() {
		return instance;
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		LOGGER.info("Initial Virtual Game Collection");
		instance = this;
		this.ticker.setCycleCount(Animation.INDEFINITE);
		this.ticker.play();
		this.rng = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void start(String[] args) throws Exception {
		LOGGER.info("Starting Virtual Game Collection");
		LOGGER.info("Version {}", Constans.VERSION);
		this.launchState = LaunchState.STARTING;
		this.stage.setScene(new Scene(new Group(), 400, 400));
		this.setScreen(new LoadingScreen());
		this.stage.show();
		handleStart(args);
		if (this.isInstantLoading()) {
			this.setScreen(new MenuScreen());
		}
	}
	
	protected void handleStart(String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<Boolean> instantLoading = parser.accepts("instantLoading").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
		} else {
			LOGGER.error("Fail to get game directory");
			ErrorWindow.make("Fail to get game directory", () -> {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Choose game directory");
				this.gameDirectory = chooser.showDialog(new Stage()).toPath();
			}).show();
		}
		if (set.has(resourceDir)) {
			this.resourceDirectory = set.valueOf(resourceDir).toPath();
		} else {
			LOGGER.info("No resource directory set, use the default directory {}/assets", this.gameDirectory);
			this.resourceDirectory = this.gameDirectory.resolve("assets");
		}
		if (set.has(instantLoading)) {
			this.instantLoading = set.valueOf(instantLoading);
			if (this.instantLoading) {
				LOGGER.info("Try instant loading");
			}
		}
	}
	
	@Override
	public void tick() {
		if (this.stage != null && this.stage.getScene() instanceof ScreenScene screenScene) {
			screenScene.tick();
		}
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public LaunchState getLaunchState() {
		return this.launchState;
	}
	
	public boolean isInstantLoading() {
		return this.instantLoading;
	}
	
	public void setScreen(Screen screen) {
		screen.init();
		this.setScene(screen.show());
	}
	
	protected void setScene(Scene scene) {
		this.initScene(scene);
		this.stage.setScene(scene);
		if (scene instanceof ScreenScene screenScene) {
			if (screenScene.shouldCenter()) {
				this.stage.centerOnScreen();
			}
		}
	}
	
	protected void initScene(Scene scene) {
		this.previousScene = this.currentScene;
		if (scene instanceof ScreenScene screenScene) {
			screenScene.setInputListeners();
			String title = screenScene.getScreen().title;
			if (title != null) {
				this.stage.setTitle(title);
			} else if (!this.stage.getTitle().trim().isEmpty()) {
				this.stage.setTitle("");
			}
		}
		this.currentScene = scene;
	}
	
	public void exit() {
		LOGGER.info("Exit Virtual Game Collection");
		this.launchState = LaunchState.STOPPING;
		Platform.exit();
		this.handleStop();
		this.launchState = LaunchState.STOPPED;
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("Stopping Virtual Game Collection");
		this.launchState = LaunchState.STOPPING;
		this.handleStop();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping Virtual Game Collection");
	}
	
	protected void handleStop() {
		this.ticker.stop();
	}
	
}
