package net.vgc.common.application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.epoll.Epoll;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.vgc.common.LaunchState;
import net.vgc.data.DataHandler;
import net.vgc.language.LanguageProvider;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;

public abstract class GameApplication extends Application implements DataHandler {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final boolean NATIVE = Epoll.isAvailable();
	protected static GameApplication instance;
	
	protected Stage stage;
	protected Random rng;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	
	public void init() throws Exception {
		instance = this;
		Thread.currentThread().setName(this.getThreadName().toLowerCase());
		this.rng = new Random(System.currentTimeMillis());
		if (this.getTicker() != null) {
			this.getTicker().play();
		}
		LOGGER.info("Initial {}", this.getName());
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.start(this.getParameters().getRaw().toArray(String[]::new));
		this.setupStage();
	}
	
	public void start(String[] args) throws Exception {
		Thread.currentThread().setName(this.getThreadName().toLowerCase());
		LOGGER.info("Starting {}", this.getName());
		this.launchState = LaunchState.STARTING;
		Network.INSTANCE.setNetworkSide(this.getNetworkSide());
		this.handleStart(args);
		this.load();
		LanguageProvider.INSTANCE.load();
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of {} with version {}", this.getName(), this.getVersion());
	}
	
	protected abstract void handleStart(String[] args) throws Exception;
	
	@Override
	public void load() throws IOException {
		
	}
	
	protected void setupStage() {
		
	}
	
	protected abstract String getThreadName();
	
	protected abstract String getName();
	
	protected abstract String getVersion();
	
	public abstract NetworkSide getNetworkSide();
	
	@Nullable
	protected Timeline getTicker() {
		return null;
	}
	
	public Stage getStage() {
		return this.stage;
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
		LOGGER.info("Exit {}", this.getName());
		Platform.exit();
	}
	
	public void stop() throws Exception {
		LOGGER.info("Stopping {}", this.getName());
		this.launchState = LaunchState.STOPPING;
		this.save();
		this.handleStop();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping {}", this.getName());
	}
	
	protected void handleStop() throws Exception {
		
	}
	
	@Override
	public void save() throws IOException {
		
	}
	
}
