package net.vgc.common.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.epoll.Epoll;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class GameApplication extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger(GameApplication.class);
	protected static final boolean NATIVE = Epoll.isAvailable();
	
	protected Stage stage;
	
	public void init() throws Exception {
		Thread.currentThread().setName("main");
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Thread.currentThread().setName(this.getThreadName().toLowerCase());
		this.stage = stage;
		this.start(this.getParameters().getRaw().toArray(String[]::new));
	}
	
	public abstract void start(String[] args) throws Exception;
	
	public Stage getStage() {
		return this.stage;
	}
	
	protected abstract String getThreadName();
	
	public void stop() throws Exception {
		
	}
	
}
