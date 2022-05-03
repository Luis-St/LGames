package net.vgc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FxTest extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Rectangle rectangle = new Rectangle(100.0, 100.0, Color.BLACK);
		rectangle.setX(50.0);
		rectangle.setY(50.0);
		stage.setScene(new Scene(new Group(rectangle), 200.0, 200.0));
		stage.show();
	}

}
