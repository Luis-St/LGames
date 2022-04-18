package net.vgc.client.fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;

public class FxUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static GridPane makeGrid(Pos pos, double gap, double padding) {
		GridPane pane = new GridPane();
		pane.setAlignment(pos);
		pane.setHgap(gap);
		pane.setVgap(gap);
		pane.setPadding(new Insets(padding));
		pane.setGridLinesVisible(Constans.DEBUG);
		return pane;
	}
	
	public static VBox makeVerticalBox(Pos pos, double padding, Node... children) {
		VBox box = new VBox();
		box.setAlignment(pos);
		box.setPadding(new Insets(padding));
		box.getChildren().addAll(children);
		return box;
	}
	
	public static HBox makeHorizontalBox(Pos pos, double padding, Node... children) {
		HBox box = new HBox();
		box.setAlignment(pos);
		box.setPadding(new Insets(padding));
		box.getChildren().addAll(children);
		return box;
	}
	
	public static Button makeButton(String name, Runnable action) {
		Button button = new Button(name);
		button.setOnAction((event) -> {
			action.run();
		});
		return button;
	}
	
	public static <T extends Region> T setResize(T region, double width, double height) {
		if (NetworkSide.CLIENT.isOn()) {
			return setResize(Client.getInstance().getStage(), region, width, height);
		}
		return region;
	}
	
	public static <T extends Region> T setResize(Stage stage, T region, double width, double height) {
		if (width > 0.0 && height > 0.0) {
			region.prefWidthProperty().bind(Bindings.multiply(stage.widthProperty(), width));
			region.prefHeightProperty().bind(Bindings.multiply(stage.heightProperty(), height));
		}
		return region;
	}
	
	public static ImageView makeImageView(String path, double width, double heigh) {
		File file = Network.INSTANCE.getResourceDirectory().resolve(path).toFile();
		ImageView imageView = null;
		try {
			imageView = new ImageView(new Image(new FileInputStream(file)));
			imageView.setFitWidth(width);
			imageView.setFitHeight(heigh);
			imageView.setSmooth(true);
		} catch (IOException e) {
			LOGGER.error("Unable to load image " + file.toPath(), e);
			throw new RuntimeException();
		}
		return imageView;
	}
	
	public static Text makeText(String displayText, double fontSize) {
		Text text = new Text(displayText);
		text.setFont(new Font(fontSize));
		return text;
	}
	
}
