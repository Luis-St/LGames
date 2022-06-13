package net.vgc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxUtil;

public class FxTest extends Application {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane pane = new BorderPane();
		pane.setLeft(new PlayerInfoPane(150.0));
		pane.setCenter(this.createGamePane());
		pane.setBottom(this.createActionPane());
		stage.setTitle("Virtual Game Collection");
		stage.setScene(new Scene(pane, 1400.0, 1100.0));
		stage.show();
	}
	
	protected GridPane createGamePane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 0.0, 20.0);
		ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < 7; i++) {
			ToggleButton button = new ToggleButton();
			button.setPrefSize(125.0, 125.0);
			button.setToggleGroup(group);
			button.setBackground(null);
			pane.add(button, i, 0);
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				pane.add(makeImageView(System.getProperty("user.home") + "/Desktop/field_overlay.png", 125.0, 125.0), i, j);
			}
		}
		return pane;
	}
	
	protected GridPane createActionPane() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 30.0);
		ButtonBox playAgainButton = new ButtonBox("Play again", Pos.CENTER, 20.0, this::handle);
		playAgainButton.getNode().setDisable(true);
		pane.addRow(0, new ButtonBox("Leave", Pos.CENTER, 20.0, this::handle), playAgainButton, new ButtonBox("Confirm action", Pos.CENTER, 20.0, this::handle));
		return pane;
	}
	
	protected void handle() {
		
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
	
	public static class PlayerInfoPane extends GridPane {
		
		protected final double separatorLength;
		
		public PlayerInfoPane(double separatorLength) {
			this.separatorLength = separatorLength;
			this.init();
		}
		
		protected void init() {
			this.setAlignment(Pos.CENTER);
			this.setVgap(10.0);
			this.setHgap(10.0);
			this.setGridLinesVisible(Constans.DEBUG);
			this.add(this.makePlayerInfoPane(), 0, 0);
			this.add(this.makeSeparator(), 0, 1);
			this.add(this.makeCurrentPlayerPane(), 0, 2);
			this.add(this.makeSeparator(), 0, 3);
			this.add(this.makePlayersPane(), 0, 4);
			this.add(this.makeSeparator(), 0, 5);
			this.add(this.makePlayerScorePane(), 0, 6);
		}
		
		protected Separator makeSeparator() {
			Separator separator = new Separator(Orientation.HORIZONTAL);
			separator.setPrefWidth(this.separatorLength);
			return separator;
		}
		
		protected GridPane makePlayerInfoPane() {
			GridPane pane = FxUtil.makeGrid(Pos.CENTER, 0.0, 5.0);
			pane.add(new Text("Player info"), 0, 0);
			return pane;
		}
		
		protected GridPane makeCurrentPlayerPane() {
			GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
			pane.add(new Text("Current player: eZ.luys"), 0, 0);
			return pane;
		}
		
		protected GridPane makePlayersPane() {
			GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
			pane.add(new Text("Yellow player: eZ.luys"), 0, 0);
			pane.add(new Text("Red player: a"), 0, 1);
			return pane;
		}
		
		protected GridPane makePlayerScorePane() {
			GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
			pane.add(new Text("eZ.luys: 0"), 0, 0);
			pane.add(new Text("a: 0"), 0, 1);
			return pane;
		}
		
	}

}
