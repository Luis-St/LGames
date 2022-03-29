package net.vgc.common.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import net.vgc.util.Tickable;

public abstract class GameApplication extends Application implements Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger(GameApplication.class);
	
	protected Stage stage;
	
	public void init() throws Exception {
		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Thread.currentThread().setName("main");
		this.stage = stage;
		this.start(this.getParameters().getRaw().toArray(String[]::new));
	}
	
	public abstract void start(String[] args) throws Exception;
	
	public void stop() throws Exception {
		
	}
	
}
