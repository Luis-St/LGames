package net.vgc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.vgc.client.fx.FxUtil;

public class Test extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 6.0, 0);
		stage.setScene(new Scene(pane, 200.0, 200.0));
		stage.show();
	}

}
