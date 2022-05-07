package net.vgc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.client.fx.FxUtil;

public class FxTest extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane outerPane = new BorderPane();
		outerPane.setCenter(new Text("center"));
		BorderPane innerPane = new BorderPane();
		ToggleGroup group = new ToggleGroup();
		ToggleButton button = new ToggleButton("Toggle");
		button.setToggleGroup(group);
		button.setOnAction((event) -> {
			group.selectToggle(null);
		});
		VBox leftBox = FxUtil.makeVerticalBox(Pos.CENTER_LEFT, 20.0, button);
		VBox rightBox = FxUtil.makeVerticalBox(Pos.CENTER_RIGHT, 20.0, FxUtil.makeButton("Reset", () -> {
			group.selectToggle(null);
		}));
		innerPane.setLeft(leftBox);
		innerPane.setRight(rightBox);
		outerPane.setBottom(innerPane);
		stage.setScene(new Scene(outerPane, 200.0, 200.0));
		stage.show();
	}

}
