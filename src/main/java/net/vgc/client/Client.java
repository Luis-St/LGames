package net.vgc.client;

import java.io.File;
import java.nio.file.Path;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.Screen;
import net.vgc.client.window.ErrorWindow;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;

public class Client extends GameApplication {
	
	protected static Client instance;
	
	protected final Timeline ticker = new Timeline(20.0, new KeyFrame(Duration.millis(50), "ClientTicker", (event) -> {
		Client.getInstance().tick();
	}));
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	protected LaunchState launchState;
	protected Scene currentScene;
	protected Scene previousScene;
	
	public static Client getInstance() {
		return instance;
	}
	
	@Override
	public void init() throws Exception {
		LOGGER.info("Initial Virtual Game Collection");
		instance = this;
		this.ticker.setCycleCount(Animation.INDEFINITE);
		this.ticker.play();
	}
	
	@Override
	public void start(String[] args) throws Exception {
		LOGGER.info("Start Virtual Game Collection");
		this.launchState = LaunchState.STARTING;
		this.stage.setScene(new Scene(new Group(), 400, 400));
		handleStart(args);
		this.setScreen(new LoadingScreen());
		this.stage.show();
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully starting of Virtual Game Collection");
	}
	
	protected void handleStart(String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
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
	}
	
	@Override
	public void tick() {
		if (this.stage != null && this.stage.getScene() instanceof ScreenScene screenScene) {
			screenScene.tick();
		}
	}
	
	public void setScreen(Screen screen) {
		screen.init();
		this.setScene(screen.show());
	}
	
	protected void setScene(Scene scene) {
		this.initScene(scene);
		this.stage.setScene(scene);
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
