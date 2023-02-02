package net.luis.game.application;

import io.netty.channel.epoll.Epoll;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.luis.game.manager.GameManager;
import net.luis.language.LanguageProvider;
import net.luis.utility.ExceptionHandler;
import net.luis.utility.data.DataHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameApplication extends Application implements DataHandler {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final boolean NATIVE = Epoll.isAvailable();
	private static GameApplication instance;
	protected Stage stage;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Path gameDirectory;
	protected Path resourceDirectory;
	private Random rng;
	
	@Nullable
	public static GameApplication getInstance() {
		return instance;
	}
	
	public void init() throws Exception {
		instance = this;
		this.initThread();
		this.rng = new Random(System.currentTimeMillis());
		if (this.getTicker() != null) {
			this.getTicker().play();
		}
		LOGGER.info("Initial {}", this.getApplicationType().getName());
	}
	
	protected final void initThread() {
		Thread currentThread = Thread.currentThread();
		currentThread.setName(this.getApplicationType().getShortName().toLowerCase());
		currentThread.setUncaughtExceptionHandler(new ExceptionHandler());
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.start(this.getParameters().getRaw().toArray(String[]::new));
		this.setupStage();
	}
	
	public void start(String[] args) throws Exception {
		this.initThread();
		LOGGER.info("Starting {}", this.getApplicationType().getName());
		this.launchState = LaunchState.STARTING;
		this.handleStart(args);
		try {
			this.load();
		} catch (IOException e) {
			LOGGER.error("Fail to load {}", this.getApplicationType().getName());
			throw new RuntimeException(e);
		}
		LanguageProvider.INSTANCE.load(this.getResourceDirectory());
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of {}", this.getApplicationType().getName());
	}
	
	protected abstract void handleStart(String[] args) throws Exception;
	
	@Override
	public void load() throws IOException {
		
	}
	
	protected void setupStage() {
		
	}
	
	public abstract ApplicationType getApplicationType();
	
	public abstract GameManager getGameManager();
	
	@Nullable
	protected Timeline getTicker() {
		return null;
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
	public Random getRNG() {
		return this.rng;
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
	
	public void exit() {
		LOGGER.info("Exit {}", this.getApplicationType().getName());
		Platform.exit();
	}
	
	public void stop() {
		LOGGER.info("Stopping {}", this.getApplicationType().getName());
		this.launchState = LaunchState.STOPPING;
		try {
			this.save();
		} catch (IOException e) {
			LOGGER.error("Fail to save {}", this.getApplicationType().getName());
			throw new RuntimeException(e);
		}
		this.handleStop();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping {}", this.getApplicationType().getName());
	}
	
	protected void handleStop() {
		
	}
	
	@Override
	public void save() throws IOException {
		
	}
	
	/**
	 *
	 * @author Luis-st
	 *
	 */
	public enum LaunchState {
		
		STARTING("starting"), STARTED("started"), STOPPING("stopping"), STOPPED("stopped"), UNKNOWN("unknown");
		
		private final String name;
		
		LaunchState(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
		
	}
}
