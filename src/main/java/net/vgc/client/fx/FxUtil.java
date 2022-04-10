package net.vgc.client.fx;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
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
import net.vgc.network.NetworkSide;

public class FxUtil {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static <T extends Node> VBox make(T node, Pos pos) {
		return make(node, pos, null);
	}
	
	public static <T extends Node> VBox make(T node, Pos pos, @Nullable Consumer<T> consumer) {
		VBox box = new VBox();
		box.setAlignment(pos);
		if (consumer != null) {
			consumer.accept(node);
		}
		box.getChildren().add(node);
		return box;
	}
	
	public static <T extends Node> VBox makeCentered(T node) {
		return makeCentered(node, null);
	}
	
	public static <T extends Node> VBox makeCentered(T node, @Nullable Consumer<T> consumer) {
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
	
	public static Button makeButton(String name, Runnable action) {
		Button button = new Button(name);
		button.setOnAction((event) -> {
			action.run();
		});
		return button;
	}
	
	public static DoubleProperty makeProperty(String name, Object bean, double value) {
		DoubleProperty property = new DoublePropertyBase() {
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public Object getBean() {
				return bean;
			}
		};
		property.set(value);
		return property;
	}
	
	public static <T extends Region> T setResize(T region, double width, double height) {
		if (NetworkSide.CLIENT.isOn()) {
			return setResize(Client.getInstance().getStage(), region, width, height);
		}
		return region;
	}
	
	public static <T extends Region> T setResize(Stage stage, T region, double width, double height) { // TODO: resize font
		if (width > 0.0 && height > 0.0) {
			region.prefWidthProperty().bind(Bindings.multiply(stage.widthProperty(), width));
			region.prefHeightProperty().bind(Bindings.multiply(stage.heightProperty(), height));
		}
		return region;
	}
	
}
