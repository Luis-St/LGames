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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FxTest extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ToggleGroup group = new ToggleGroup();
		group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info("Trigger");
			if (oldValue != null && newValue == null) {
				oldValue.setSelected(true);
			}
		});
		ToggleButton button1 = new ToggleButton("Test 1");
		button1.setToggleGroup(group);
		button1.setOnAction((event) -> {
			LOGGER.info("Test 1");
		});
		ToggleButton button2 = new ToggleButton("Test 2");
		button2.setToggleGroup(group);
		button2.setOnAction((event) -> {
			LOGGER.info("Test 2");
		});
		stage.setScene(new Scene(new HBox(button1, button2), 200.0, 200.0));
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
