package net.vgc.client.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import net.vgc.Constans;
import net.vgc.client.fx.ScreenScene;

public class LoadingScreen extends Screen {
	
	protected ProgressBar bar;
	protected boolean enterMenu;
	protected int ticks;
	
	public LoadingScreen() {
		this.title = "Loading " + Constans.NAME;
		this.width = 300;
		this.height = 450;
	}
	
	@Override
	public void init() {
		this.bar = new ProgressBar();
		this.enterMenu = false;
	}
	
	@Override
	public void tick() {
		this.ticks++;
		double progress = this.bar.getProgress();
		if (progress > 1.0) {
			this.bar.setProgress(1.0);
		} else if (progress == 1.0 && !enterMenu) {
			this.client.setScreen(new MenuScreen());	
			this.enterMenu = true;
		} else {
			if (0 > progress) {
				progress = 0;
			}
			this.bar.setProgress(progress += 0.01);
		}
	}
	
	@Override
	public ScreenScene show() { // TODO: dimensions of window
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		pane.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));
		pane.add(this.bar, 0, 0);
		return new ScreenScene(pane, this.width, this.height, this);
	}
	
}
