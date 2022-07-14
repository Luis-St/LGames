package net.vgc.client.games.wins4.map.field;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.game.wrapper.LabelWrapper;
import net.vgc.client.game.map.field.AbstractClientGameField;
import net.vgc.game.GameResult;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.wins4.map.field.Wins4FieldType;
import net.vgc.games.wins4.player.Wins4PlayerType;
import net.vgc.util.ToString;

public class Wins4ClientField extends AbstractClientGameField implements LabelWrapper {
	
	private final Label label;
	
	public Wins4ClientField(Client client, GameMap map, GameFieldPos fieldPos, double size) {
		super(client, map, Wins4FieldType.DEFAULT, Wins4PlayerType.NO, fieldPos, size);
		this.label = new Label();
		this.init();
	}
	
	@Override
	public Label getLabel() {
		return this.label;
	}
	
	@Override
	public void init() {
		this.setPrefSize(this.getSize(), this.getSize());
		this.setFocusTraversable(false);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}
	
	@Override
	public final GameFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since 4 wins fields does not have a field type", this.getFieldPos().getPosition());
		return super.getFieldType();
	}

	@Override
	public final GamePlayerType getColorType() {
		LOGGER.warn("Fail to get field color type of field {}, since 4 wins fields does not have a field color type", this.getFieldPos().getPosition());
		return super.getColorType();
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
			ImageView figure = this.getFigure().getPlayer().getPlayerType().getImage(this.getSize(), this.getSize());
			if (figure != null) {
				pane.getChildren().add(figure);
			}
			if (this.isShadowed()) {
				pane.getChildren().add(this.makeImage("textures/wins4/figure_shadow.png"));
			}
			this.setGraphic(pane);
		}
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "fieldType", "colorType", "result");
	}

}
