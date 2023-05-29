package net.luis.fx.window;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractWindow {
	
	protected final Stage stage;
	private final double width;
	private final double height;
	
	protected AbstractWindow(Stage stage, double width, double height) {
		this.stage = Objects.requireNonNull(stage, "Stage must not be null");
		this.width = width;
		this.height = height;
		this.stage.setOnCloseRequest((event) -> this.exit());
	}
	
	protected void updateScene(Pane pane) {
		Scene scene = new Scene(Objects.requireNonNull(pane, "Pane must not be null"), this.width, this.height);
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
