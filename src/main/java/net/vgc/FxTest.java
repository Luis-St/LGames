package net.vgc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FxTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Button button = new Button("Test");
		button.setDisable(true);
		stage.setScene(new Scene(button));
		stage.show();
	}

}
