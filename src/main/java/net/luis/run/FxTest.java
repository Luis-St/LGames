package net.luis.run;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public class FxTest extends Application {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static ImageView makeImageView(String path, double width, double height) {
		ImageView imageView;
		try {
			imageView = new ImageView(new Image(new FileInputStream(path)));
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);
			imageView.setSmooth(true);
		} catch (IOException e) {
			LOGGER.error("Fail to load image " + path, e);
			throw new RuntimeException();
		}
		return imageView;
	}
	
	@Override
	public void start(Stage stage) {
		StackPane pane = new StackPane();
		pane.getChildren().add(new Button("Dummy"));
		pane.getChildren().add(new Text("Test"));
		stage.setTitle("Virtual Game Collection");
		stage.setScene(new Scene(pane, 250.0, 250.0));
		stage.show();
	}
	
}
