package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import net.luis.client.ClientSettings;
import net.luis.client.fx.ButtonBox;
import net.luis.fxutils.FxUtils;
import net.luis.language.Language;
import net.luis.language.TranslationKey;

/**
 *
 * @author Luis-st
 *
 */

public class SettingsScreen extends Screen {
	
	private final Screen backScreen;
	private ComboBox<String> languageSettingBox;
	private GridPane languageSetting;
	private ButtonBox backButtonBox;
	
	public SettingsScreen(Screen backScreen) {
		this.backScreen = backScreen;
	}
	
	@Override
	public void init() {
		this.languageSettingBox = new ComboBox<>();
		this.languageSettingBox.getItems().addAll(ClientSettings.LANGUAGE.getPossibleValues().stream().map(Language::name).toList());
		this.languageSettingBox.getSelectionModel().select(ClientSettings.LANGUAGE.getValue().name());
		this.languageSettingBox.setTooltip(new Tooltip(ClientSettings.LANGUAGE.getDescription()));
		this.languageSettingBox.setOnAction((event) -> {
			ClientSettings.LANGUAGE.setValue(this.languageSettingBox.getSelectionModel().getSelectedItem());
			this.reapplyScreen();
		});
		this.languageSetting = FxUtils.makeGrid(Pos.CENTER, 75.0, 10.0, 20.0);
		this.languageSetting.addRow(0, new Text(ClientSettings.LANGUAGE.getName()), this.languageSettingBox);
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
	}
	
	private void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	@Override
	protected Pane createPane() {
		GridPane gridPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane settingsGridPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		settingsGridPane.addRow(0, this.languageSetting);
		gridPane.addRow(0, settingsGridPane);
		gridPane.addRow(1, this.backButtonBox);
		return gridPane;
	}
	
}
