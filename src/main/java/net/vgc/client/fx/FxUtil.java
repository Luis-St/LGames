package net.vgc.client.fx;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.util.Util;

public class FxUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static <T extends Node> Node makeCentered(T node, @Nullable Consumer<T> consumer) {
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		if (consumer != null) {
			consumer.accept(node);
		}
		box.getChildren().add(node);
		return box;
	}
	
	public static GridPane makeGrid(Pos pos, double gap, double padding) {
		GridPane pane = new GridPane();
		pane.setAlignment(pos);
		pane.setHgap(6.0);
		pane.setVgap(6.0);
		pane.setPadding(new Insets(padding));
		pane.setGridLinesVisible(Constans.DEBUG);
		return pane;
	}
	
	public static VBox makeVerticalBox(Pos pos, double padding) {
		VBox box = new VBox();
		box.setAlignment(pos);
		box.setPadding(new Insets(padding));
		return box;
	}
	
	public static HBox makeHorizontalBox(Pos pos, double padding) {
		HBox box = new HBox();
		box.setAlignment(pos);
		box.setPadding(new Insets(padding));
		return box;
	}
	
	public static Button makeButton(String name, Runnable action, @Nullable Consumer<Button> consumer) {
		Button button = new Button(name);
		button.setOnAction((event) -> {
			action.run();
		});
		if (consumer != null) {
			consumer.accept(button);
		}
		return button;
	}
	
	public static void resize(Region region, double width, double height) {
		if (Util.isClient() && width > 0.0 && height > 0.0) {
			Stage stage = Client.getInstance().getStage();
			region.prefWidthProperty().bind(Bindings.multiply(stage.widthProperty(), width));
			region.prefHeightProperty().bind(Bindings.multiply(stage.heightProperty(), height));
		}
	}
	
}
