package net.luis.window;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.luis.application.GameApplication;
import net.luis.language.TranslationKey;
import net.luis.util.ErrorLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class ErrorWindow extends AbstractWindow {
	
	private static final Logger LOGGER = LogManager.getLogger(ErrorWindow.class);
	
	private final String title;
	private final String errorMessage;
	private final Runnable action;
	private ErrorLevel errorLevel = ErrorLevel.NO;
	
	private ErrorWindow(String title, String errorMessage, Runnable action) {
		super(new Stage(), 200.0, 100.0);
		this.title = title;
		this.errorMessage = errorMessage.toLowerCase().trim().length() != 0 ? errorMessage : null;
		this.action = action;
	}
	
	public static ErrorWindow makeCrash(String errorMessage) {
		return new ErrorWindow("Error", errorMessage, () -> {
			Objects.requireNonNull(GameApplication.getInstance()).exit();
			LOGGER.trace("Something went wrong while handle a critical error");
		}).setErrorLevel(ErrorLevel.CRITICAL);
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
	
	public ErrorLevel getErrorLevel() {
		return this.errorLevel;
	}
	
	public ErrorWindow setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
		return this;
	}
	
	@Override
	public void show() {
		GridPane pane = new GridPane();
		double width = 200.0;
		double height = 100.0;
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10.0);
		pane.setVgap(10.0);
		boolean flag = this.errorMessage != null;
		if (flag) {
			Text text = new Text(this.errorMessage);
			text.setFill(this.errorLevel.getColor());
			text.setFill(Color.RED);
			pane.add(text, 0, 0);
			width = Math.max(200.0, text.getLayoutBounds().getWidth() + 10.0);
			double d = text.getLayoutBounds().getHeight();
			if (d > 60.0) {
				height = Math.max(100.0, d + 50.0);
			}
		}
		Button button = new Button(TranslationKey.createAndGet("window.error.continue"));
		button.setOnAction((event) -> {
			this.close();
			this.action.run();
		});
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.getChildren().add(button);
		pane.add(box, 0, flag ? 1 : 0);
		this.updateScene(pane);
		this.stage.setTitle(this.title);
		this.stage.showAndWait();
	}
	
	@Override
	protected void exit() {
	
	}
	
}
