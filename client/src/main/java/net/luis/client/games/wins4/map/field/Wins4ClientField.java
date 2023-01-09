package net.luis.client.games.wins4.map.field;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.luis.client.Client;
import net.luis.client.fx.game.wrapper.LabelWrapper;
import net.luis.client.game.map.field.AbstractClientGameField;
import net.luis.common.Constants;
import net.luis.common.player.GameProfile;
import net.luis.game.GameResult;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.wins4.map.field.Wins4FieldType;
import net.luis.games.wins4.player.Wins4PlayerType;
import net.luis.utils.util.ToString;
import net.luis.utils.util.Utils;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ClientField extends AbstractClientGameField implements LabelWrapper {
	
	private final Label label;
	
	public Wins4ClientField(Client client, GameMap map, GameFieldPos fieldPos, double size) {
		super(client, map, Wins4FieldType.DEFAULT, Wins4PlayerType.NO, fieldPos, size);
		this.label = new Label();
		this.init();
	}
	
	@Override
	public Label getLabel() {
		this.label.setUserData(this);
		return this.label;
	}
	
	@Override
	public void init() {
		this.setPrefSize(this.getSize(), this.getSize());
		this.setFocusTraversable(false);
		if (!Constants.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}
	
	@Override
	public final GameFieldType getFieldType() {
		GameField.LOGGER.warn("Fail to get field type of field {}, since 4 wins fields does not have a field type", this.getFieldPos().getPosition());
		return super.getFieldType();
	}
	
	@Override
	public final GamePlayerType getColorType() {
		GameField.LOGGER.warn("Fail to get field color type of field {}, since 4 wins fields does not have a field color type", this.getFieldPos().getPosition());
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
			case GameResult.WIN -> this.makeImage("textures/wins4/field/field_background_win.png");
			case GameResult.LOSE -> this.makeImage("textures/wins4/field/field_background_lose.png");
			case GameResult.DRAW -> this.makeImage("textures/wins4/field/field_background_draw.png");
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
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.getFieldPos(), GameProfile.EMPTY, -1, Utils.EMPTY_UUID);
		}
		GameFigure figure = this.getFigure();
		assert figure != null;
		return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.getFieldPos(), figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "fieldType", "colorType", "result");
	}
	
}
