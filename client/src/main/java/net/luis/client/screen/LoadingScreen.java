package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.luis.fx.Box;
import net.luis.fxutils.FxUtils;
import net.luis.language.TranslationKey;
import net.luis.utils.math.Mth;

/**
 *
 * @author Luis-st
 *
 */

public class LoadingScreen extends ClientScreen {
	
	private Box<Text> vgcTextBox;
	private Box<Text> loadingTextBox;
	private Box<ProgressBar> loadingBarBox;
	private boolean enterMenu;
	
	public LoadingScreen() {
		super(TranslationKey.createAndGet("screen.loading.title"), 400, 550);
	}
	
	@Override
	public void init() {
		Text vgcText = new Text(TranslationKey.createAndGet("client.constans.name"));
		vgcText.setFont(new Font(25.0));
		this.vgcTextBox = new Box<>(vgcText);
		Text loadingText = new Text(TranslationKey.createAndGet("screen.loading.loading.text", 0.0));
		this.loadingTextBox = new Box<>(loadingText);
		ProgressBar loadingBar = new ProgressBar();
		loadingBar.setPrefSize(250.0, 20.0);
		loadingBar.progressProperty().addListener((observable, oldValue, newValue) -> {
			loadingText.setText(TranslationKey.createAndGet("screen.loading.loading.text", Mth.roundTo(newValue.doubleValue() * 100, 100)));
		});
		this.loadingBarBox = new Box<>(loadingBar);
		this.enterMenu = false;
	}
	
	@Override
	public void tick() {
		double progress = this.loadingBarBox.getNode().getProgress();
		if (progress > 1.0) {
			this.loadingBarBox.getNode().setProgress(1.0);
		} else if (progress == 1.0 && !enterMenu) {
			this.client.setScreen(new MenuScreen());
			this.enterMenu = true;
		} else {
			if (0 > progress) {
				progress = 0;
			}
			this.loadingBarBox.getNode().setProgress(progress + (this.client.isSafeLoading() && !this.client.isInstantLoading() ? 0.001 : 0.01));
		}
	}
	
	@Override
	protected Pane createPane() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		pane.add(this.vgcTextBox, 0, 0);
		pane.add(this.loadingBarBox, 0, 10);
		pane.add(this.loadingTextBox, 0, 10);
		return pane;
	}
	
}
