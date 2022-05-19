package net.vgc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FxTest extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ToggleButton button = new ToggleButton("Test");
		button.setToggleGroup(new ToggleGroup());
		button.setOnAction((event) -> {
			LOGGER.info("Test");
		});
		stage.setScene(new Scene(button, 200.0, 200.0));
		stage.show();
	}
	
	@Nullable	
	public static ImageView makeImageView(String path, double width, double heigh) {
		ImageView imageView = null;
		try {
			imageView = new ImageView(new Image(new FileInputStream(new File(path))));
			imageView.setFitWidth(width);
			imageView.setFitHeight(heigh);
			imageView.setSmooth(true);
		} catch (IOException e) {
			LOGGER.error("Fail to load image " + path, e);
			throw new RuntimeException();
		}
		return imageView;
	}

}
