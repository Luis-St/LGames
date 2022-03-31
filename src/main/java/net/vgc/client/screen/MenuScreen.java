package net.vgc.client.screen;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.vgc.Constans;
import net.vgc.client.fx.FxUtil;

public class MenuScreen extends Screen {

	public MenuScreen() {
		this.title = Constans.NAME;
		this.width = 1000;
		this.height = 800;
		this.shouldCenter = true;
	}

	@Override
	public Scene show() {
		BorderPane border = new BorderPane();
		VBox centerBox = FxUtil.makeVerticalBox(Pos.CENTER, 0.0);
		GridPane grid = FxUtil.makeGrid(Pos.CENTER, 6.0, 10.0);
		Node singleplayer = FxUtil.makeCentered(new Button("Singleplayer"), (button) -> {
			button.setOnAction(this::handleSingleplayer);
			FxUtil.resize(button, 0.5, 0.1);
		});
		Node multiplayer = FxUtil.makeCentered(new Button("Multiplayer"), (button) -> {
			button.setOnAction(this::handleMultiplayer);
			FxUtil.resize(button, 0.5, 0.1);
		});
		Node settings = FxUtil.makeCentered(new Button("Settings"), (button) -> {
			button.setOnAction(this::handleSettings);
			FxUtil.resize(button, 0.5, 0.1);
		});
		grid.addColumn(0, singleplayer, multiplayer, settings);
		BorderPane.setAlignment(centerBox, Pos.CENTER);
		centerBox.getChildren().add(grid);
		VBox topBox = FxUtil.makeVerticalBox(Pos.CENTER_RIGHT, 20.0);
		topBox.getChildren().add(FxUtil.makeButton("Profile", this::handleProfile, (button) -> {
			FxUtil.resize(button, 0.0, 0.0);
		}));
		BorderPane.setAlignment(topBox, Pos.CENTER_RIGHT);
		border.setTop(topBox);
		border.setCenter(centerBox);
		return new Scene(border, this.width, this.height);
	}
	
	protected void handleSingleplayer(ActionEvent event) { // TODO: impl
		LOGGER.debug("Singleplayer");
	}
	
	protected void handleMultiplayer(ActionEvent event) { // TODO: impl
		LOGGER.debug("Multiplayer");
	}
	
	protected void handleSettings(ActionEvent event) { // TODO: impl
		LOGGER.debug("Settings");
	}
	
	protected void handleProfile() { // TODO: impl
		LOGGER.debug("Profile");
	}

}
