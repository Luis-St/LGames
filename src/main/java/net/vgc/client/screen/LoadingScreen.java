package net.vgc.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.vgc.Constans;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.ScreenScene;
import net.vgc.util.Mth;

public class LoadingScreen extends Screen {
	
	protected Text vgcText;
	protected VBox vgcTextBox;
	protected Text loadingText;
	protected VBox loadingTextBox;
	protected ProgressBar loadingBar;
	protected boolean enterMenu;
	
	public LoadingScreen() {
		this.title = "Loading " + Constans.NAME;
		this.width = 400;
		this.height = 550;
	}
	
	@Override
	public void init() {
		this.vgcText = new Text(Constans.NAME);
		this.vgcText.setFont(new Font(25.0));
		this.vgcTextBox = FxUtil.makeCentered(this.vgcText);
		this.loadingText = new Text("Loading 0%");
		this.loadingTextBox = FxUtil.makeCentered(this.loadingText);
		this.loadingBar = new ProgressBar();
		FxUtil.setResize(this.loadingBar, 0.75, 0.04);
		this.loadingBar.progressProperty().addListener((observable, oldValue, newValue) -> {
			this.loadingText.setText("Loading " + Mth.roundTo(newValue.doubleValue() * 100, 100) + "%");
		});
		this.enterMenu = false;
	}
	
	@Override
	public void tick() {
		double progress = this.loadingBar.getProgress();
		if (progress > 1.0) {
			this.loadingBar.setProgress(1.0);
		} else if (progress == 1.0 && !enterMenu) {
			this.client.setScreen(new MenuScreen());	
			this.enterMenu = true;
		} else {
			if (0 > progress) {
				progress = 0;
			}
			this.loadingBar.setProgress(progress += this.client.isSafeLoading() && !this.client.isInstantLoading() ? 0.001 : 0.01);
		}
	}
	
	@Override
	public ScreenScene show() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.add(this.vgcTextBox, 0, 0);
		pane.add(this.loadingBar, 0, 10);
		pane.add(this.loadingTextBox, 0, 10);
		return new ScreenScene(pane, this.width, this.height, this);
	}
	
}
