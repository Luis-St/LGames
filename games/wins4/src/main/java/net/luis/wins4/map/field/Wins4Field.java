package net.luis.wins4.map.field;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.luis.Constants;
import net.luis.fx.game.wrapper.LabelWrapper;
import net.luis.game.GameResult;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.wins4.player.Wins4PlayerType;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4Field extends AbstractGameField implements LabelWrapper {
	
	private final Label label = new Label();
	
	public Wins4Field(GameMap map, GameFieldPos fieldPos) {
		super(map, Wins4FieldType.DEFAULT, Wins4PlayerType.NO, fieldPos, 120.0);
	}
	
	@Override
	public Label getLabel() {
		this.label.setUserData(this);
		return this.label;
	}
	
	@Override
	public void init() {
		this.setPrefSize(this.fieldSize, this.fieldSize);
		this.setFocusTraversable(false);
		if (!Constants.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}
	
	@Override
	public boolean isHome() {
		return false;
	}
	
	@Override
	public boolean isStart() {
		return false;
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return false;
	}
	
	@Override
	public boolean isWin() {
		return false;
	}
	
	@Override
	public ImageView getFieldBackground() {
		return switch (this.getResult()) {
			case WIN -> this.makeImage("textures/wins4/field/field_background_win.png");
			case LOSE -> this.makeImage("textures/wins4/field/field_background_lose.png");
			case DRAW -> this.makeImage("textures/wins4/field/field_background_draw.png");
			default -> this.makeImage("textures/wins4/field/field_background.png");
		};
	}
	
	@Override
	public boolean canSelect() {
		return this.isEmpty() && this.getResult() == GameResult.NO;
	}
	
	@Override
	public void updateFieldGraphic() {
		ImageView fieldBackground = this.getFieldBackground();
		StackPane pane = new StackPane(fieldBackground);
		if (this.isEmpty()) {
			if (this.isShadowed()) {
				pane.getChildren().add(this.makeImage("textures/wins4/figure/figure_shadow.png"));
			}
			this.setGraphic(pane);
		} else {
			assert this.getFigure() != null;
			ImageView figure = this.getFigure().getPlayer().getPlayerType().getImage(this.fieldSize, this.fieldSize);
			if (figure != null) {
				pane.getChildren().add(figure);
			}
			if (this.isShadowed()) {
				pane.getChildren().add(this.makeImage("textures/wins4/figure_shadow.png"));
			}
			this.setGraphic(pane);
		}
	}
	
}
