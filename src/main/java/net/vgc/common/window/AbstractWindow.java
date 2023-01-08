package net.vgc.common.window;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger(ErrorWindow.class);
	
	protected final Stage stage;
	private final double width;
	private final double height;
	
	protected AbstractWindow(Stage stage, double width, double height) {
		this.stage = stage;
		this.width = width;
		this.height = height;
		this.stage.setOnCloseRequest((event) -> this.exit());
	}
	
	protected void updateScene(Pane pane) {
		Scene scene = new Scene(pane, this.width, this.height);
		this.onUpdateScene(scene);
		this.stage.setScene(scene);
	}
	
	protected void onUpdateScene(Scene scene) {
	
	}
	
	public abstract void show();
	
	protected abstract void exit();
	
	public void close() {
		this.exit();
		this.stage.close();
	}
	
}
