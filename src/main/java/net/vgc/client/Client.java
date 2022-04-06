package net.vgc.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import javax.annotation.Nullable;

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
import net.vgc.client.fx.Screenable;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.screen.Screen;
import net.vgc.client.window.ErrorWindow;
import net.vgc.common.ErrorLevel;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;
import net.vgc.network.Connection;
import net.vgc.util.Tickable;
import net.vgc.util.Util;

public class Client extends GameApplication  implements Tickable, Screenable {
	
	protected static Client instance;
	
	@Nullable
	public static Client getInstance() {
		if (Util.isClient()) {
			return instance;
		}
		return null;
	}
	
	protected final Timeline ticker = Util.createTicker("ClientTicker", () -> {
		Client.getInstance().tick();
	});
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Scene currentScene;
	protected Scene previousScene;
	protected boolean instantLoading;
	protected boolean safeLoading;
	protected Random rng;
	protected Connection serverConnection;
	protected Connection accountServerConnection;
	
	@Override
	public void init() throws Exception {
		super.init();
		LOGGER.info("Initial Virtual Game Collection");
		instance = this;
		this.ticker.play();
		this.rng = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void start(String[] args) throws Exception {
		LOGGER.info("Starting Virtual Game Collection");
		this.launchState = LaunchState.STARTING;
		this.stage.setScene(new Scene(new Group(), 400, 400));
		this.setScreen(new LoadingScreen());
		this.stage.show();
		this.handleStart(args);
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of Virtual Game Collection with version {}", Constans.Client.VERSION);
		if (this.isInstantLoading()) {
			this.setScreen(new MenuScreen());
			this.stage.centerOnScreen();
		}
	}
	
	protected void handleStart(String[] args) throws Exception { // TODO: create Loading Steps, which are load from 0 til 1
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<Boolean> instantLoading = parser.accepts("instantLoading").withRequiredArg().ofType(Boolean.class);
		OptionSpec<Boolean> safeLoading = parser.accepts("safeLoading").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
			LOGGER.debug("Set game directory to {}", this.gameDirectory);
		} else {
			LOGGER.warn("Fail to get game directory");
			ErrorWindow.make("Fail to get game directory", () -> {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Choose game directory");
				this.gameDirectory = chooser.showDialog(new Stage()).toPath();
				LOGGER.debug("Set game directory to {}", this.gameDirectory);
			}).setErrorLevel(ErrorLevel.ERROR).show(); // TODO: stop loading
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
		}
		if (set.has(resourceDir)) {
			this.resourceDirectory = set.valueOf(resourceDir).toPath();
			LOGGER.debug("Set resource directory to {}", this.resourceDirectory);
		} else {
			LOGGER.info("No resource directory set, use the default directory {}/assets", this.gameDirectory);
			this.resourceDirectory = this.gameDirectory.resolve("assets");
		}
		if (!Files.exists(this.resourceDirectory)) {
			Files.createDirectories(this.resourceDirectory);
			LOGGER.debug("Create Client directory");
		}
		if (set.has(instantLoading)) {
			if (this.instantLoading = set.valueOf(instantLoading)) {
				LOGGER.info("Try instant loading");
			} else if (set.has(safeLoading)) {
				this.safeLoading = set.valueOf(safeLoading);
				LOGGER.info("Use safe loading");
			}
		}

	}
	
	@Override
	public void tick() {
		if (this.stage != null && this.stage.getScene() instanceof ScreenScene screenScene) {
			screenScene.tick();
		}
	}
	
	public void connectServer() {
		// TODO Auto-generated method stub
		
	}
	
	public void disconnectServer() {
		// TODO Auto-generated method stub
		
	}
	
	public void connectAccountServer() {
		// TODO Auto-generated method stub
		
	}
	
	public void disconnectAccountServer() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected String getThreadName() {
		return "client";
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
	
	public boolean isRunning() {
		return this.launchState == LaunchState.STARTED;
	}
	
	public boolean isInstantLoading() {
		return this.instantLoading;
	}
	
	public boolean isSafeLoading() {
		return this.safeLoading;
	}
	
	@Override
	public void setScreen(Screen screen) {
		this.initScreen(screen);
		this.setScene(screen.show());
		LOGGER.debug("Update Screen to {}", screen.getClass().getSimpleName());
	}
	
	protected void initScreen(Screen screen) {
		screen.init();
		if (screen.title != null) {
			this.stage.setTitle(screen.title);
		} else if (!this.stage.getTitle().trim().isEmpty()) {
			this.stage.setTitle("");
		}
		if (screen.shouldCenter) {
			this.stage.centerOnScreen();
		}
	}
	
	protected void setScene(Scene scene) {
		this.previousScene = this.currentScene;
		if (scene instanceof ScreenScene screenScene) {
			screenScene.setInputListeners();
		}
		this.currentScene = scene;
		this.stage.setScene(scene);
	}
	
	public void exit() {
		LOGGER.info("Exit Virtual Game Collection");
		Platform.exit();
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
