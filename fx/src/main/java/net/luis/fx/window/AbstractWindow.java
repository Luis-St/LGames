package net.luis.fx.window;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractWindow {
	
	protected final Stage stage;
	private final double width;
	private final double height;
	
	protected AbstractWindow(@NotNull Stage stage, double width, double height) {
		this.stage = stage;
		this.width = width;
		this.height = height;
		this.stage.setOnCloseRequest((event) -> this.exit());
	}
	
	protected void updateScene(@NotNull Pane pane) {
		Scene scene = new Scene(pane, this.width, this.height);
		this.onUpdateScene(scene);
		this.stage.setScene(scene);
	}
	
	protected void onUpdateScene(@NotNull Scene scene) {
	
	}
	
	public abstract void show();
	
	protected abstract void exit();
	
	public void close() {
		this.exit();
		this.stage.close();
	}
	
}
