package net.vgc.client.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import net.vgc.client.fx.ScreenScene;

public class LoadingScreen extends Screen {
	
	protected ProgressBar bar;
	
	public LoadingScreen() {
		this.title = "Loading";
		this.width = 300;
		this.height = 450;
	}
	
	@Override
	public void init() {
		this.bar = new ProgressBar();
		this.bar.prefWidth(2000);
		this.bar.prefHeight(500);
	}
	
	@Override
	public void tick() {
		double progress = this.bar.getProgress();
		if (progress >= 1.0) {
			this.bar.setProgress(0.0);
		} else {
			this.bar.setProgress(progress + 0.01);
		}
	}
	
	@Override
	public ScreenScene show() {
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		pane.setPadding(new Insets(25.0, 25.0, 25.0, 25.0));
		pane.add(this.bar, 0, 0);
		return new ScreenScene(pane, this.width, this.height, this);
	}
	
}
