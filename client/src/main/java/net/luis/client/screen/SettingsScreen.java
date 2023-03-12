package net.luis.client.screen;

/**
 *
 * @author Luis-st
 *
 */

/*
public class SettingsScreen extends ClientScreen {
	
	private final ClientScreen backScreen;
	private ComboBox<String> languageSettingBox;
	private GridPane languageSetting;
	private ButtonBox backButtonBox;
	
	public SettingsScreen(ClientScreen backScreen) {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
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
*/
