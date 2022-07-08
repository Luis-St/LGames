package net.vgc.client.fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.vgc.Constans;
import net.vgc.network.Network;

public class FxUtil {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static GridPane makeGrid(Pos pos, double gap, double padding) {
		return makeGrid(pos, gap, gap, padding);
	}
	
	public static GridPane makeGrid(Pos pos, double hGap, double vGap, double padding) {
		GridPane pane = new GridPane();
		pane.setAlignment(pos);
		pane.setHgap(hGap);
		pane.setVgap(vGap);
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
	
	@Nullable	
	public static ImageView makeImageView(String path, double width, double heigh) {
		ImageView imageView = null;
		if (path != null) {
			File file = Network.INSTANCE.getResourceDirectory().resolve(path).toFile();
			try {
				imageView = new ImageView(new Image(new FileInputStream(file)));
				imageView.setFitWidth(width);
				imageView.setFitHeight(heigh);
				imageView.setSmooth(true);
			} catch (IOException e) {
				LOGGER.error("Fail to load image " + file.toPath(), e);
				throw new RuntimeException();
			}
		}
		return imageView;
	}
	
	public static Text makeText(String displayText, double fontSize) {
		Text text = new Text(displayText);
		text.setFont(new Font(fontSize));
		return text;
	}
	
}
