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
import net.vgc.common.loading.LaunchState;
import net.vgc.data.DataHandler;
import net.vgc.language.LanguageProvider;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.PacketListener;
import net.vgc.util.ExceptionHandler;

public abstract class GameApplication<T extends PacketListener> extends Application implements DataHandler, PacketHandler<Packet<T>> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final boolean NATIVE = Epoll.isAvailable();
	protected static GameApplication<?> instance;
	
	protected Stage stage;
	protected Random rng;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	
	public void init() throws Exception {
		instance = this;
		this.initThread();
		this.rng = new Random(System.currentTimeMillis());
		if (this.getTicker() != null) {
			this.getTicker().play();
		}
		LOGGER.info("Initial {}", this.getName());
	}
	
	protected final void initThread() {
		Thread currentThread = Thread.currentThread();
		currentThread.setName(this.getThreadName().toLowerCase());
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
		LOGGER.info("Starting {}", this.getName());
		this.launchState = LaunchState.STARTING;
		Network.INSTANCE.setNetworkSide(this.getNetworkSide());
		this.handleStart(args);
		try {
			this.load();
		} catch (IOException e) {
			LOGGER.error("Fail to load {}", this.getName());
			throw new RuntimeException(e);
		}
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
	
	@Override
	public void handlePacket(Packet<T> packet) {
		
	}
	
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
		try {
			this.save();
		} catch (IOException e) {
			LOGGER.error("Fail to save {}", this.getName());
			throw new RuntimeException(e);
		}
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
