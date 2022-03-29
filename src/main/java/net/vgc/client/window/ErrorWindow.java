package net.vgc.client.window;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.client.Client;
import net.vgc.server.Server;

public class ErrorWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger(ErrorWindow.class);
	
	protected final String title;
	protected final String errorMessage;
	protected final Runnable action;
	
	protected ErrorWindow(String title, String errorMessage, Runnable action) {
		this.title = title;
		this.errorMessage = errorMessage.toLowerCase().trim().length() != 0 ? errorMessage : null;
		this.action = action;
	}
	
	public static ErrorWindow makeCrash(String errorMessage) {
		return new ErrorWindow("Error", errorMessage, () -> {
			if (System.getProperty("net.vgc.launch.type").equalsIgnoreCase("client")) {
				Client.getInstance().exit();
			} else if (System.getProperty("net.vgc.launch.type").equalsIgnoreCase("server")) {
				Server.getInstance().exit();
			}
			LOGGER.trace("Something went wrong while handle a critical error");
			System.exit(-1);
		});
	}
	
	public static ErrorWindow make(String errorMessage, Runnable action) {
		return new ErrorWindow("Error", errorMessage, action);
	}
	
	public static ErrorWindow make(String title, String errorMessage, Runnable action) {
		return new ErrorWindow(title, errorMessage, action);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	@Nullable
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public void show() {
		Stage stage = new Stage();
		GridPane pane = new GridPane();
		double width = 200.0;
		double height = 100.0;
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		boolean flag = this.errorMessage != null;
		if (flag) {
			Text text = new Text(this.errorMessage);
			pane.add(text, 0, 0);
			width = Math.max(200.0, text.getLayoutBounds().getWidth() + 10.0);
			double d = text.getLayoutBounds().getHeight();
			if (d > 60.0) {
				height = Math.max(100.0, d + 50.0);
			}
		}
		Button button = new Button("Continue");
		button.setOnAction((event) -> {
			stage.close();
			this.action.run();
		});
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.getChildren().add(button);
		pane.add(box, 0, flag ? 1 : 0);

		stage.setScene(new Scene(pane, width, height));
		stage.setTitle(this.title);
		stage.showAndWait();
	}
	
}