package net.luis.fx.screen;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.fx.ScreenScene;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractScreen {
	
	private final String title;
	private final int width;
	private final int height;
	private final boolean resizable;
	
	public AbstractScreen(String title, int width, int height) {
		this(title, width, height, true);
	}
	
	public AbstractScreen(String title, int width, int height, boolean resizable) {
		this.title = Objects.requireNonNull(title, "Title must not be null");
		this.width = width;
		this.height = height;
		this.resizable = resizable;
	}
	
	public void init() {
		
	}
	
	public @NotNull String getTitle() {
		return StringUtils.trimToEmpty(this.title);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void refresh() {
	
	}
	
	protected abstract @NotNull Pane createPane();
	
	protected @NotNull ScreenScene createScene() {
		return new ScreenScene(this.createPane(), this.width, this.height, this);
	}
	
	public final void show(Stage stage) {
		Objects.requireNonNull(stage, "Stage must not be null");
		stage.setScene(this.createScene());
		stage.setResizable(this.resizable);
	}
}
