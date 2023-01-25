package net.luis.client.games.ludo.map.field;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.luis.Constants;
import net.luis.client.Client;
import net.luis.client.fx.Box;
import net.luis.client.game.map.field.AbstractClientGameField;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.GameResult;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.ludo.map.field.LudoFieldType;
import net.luis.games.ludo.player.LudoPlayerType;
import net.luis.utils.util.ToString;

/**
 *
 * @author Luis-st
 *
 */

public class LudoClientField extends AbstractClientGameField implements ToggleButtonWrapper {
	
	private final ToggleButton button;
	private final ToggleGroup group;
	
	public LudoClientField(Client client, GameMap map, ToggleGroup group, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos, double size) {
		super(client, map, fieldType, colorType, fieldPos, size);
		this.button = new ToggleButton();
		this.group = group;
		this.init();
	}
	
	@Override
	public ToggleButton getToggleButton() {
		this.button.setUserData(this);
		return this.button;
	}
	
	@Override
	public void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.getSize(), this.getSize());
		this.setFocusTraversable(false);
		if (!Constants.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}
	
	@Override
	public boolean isHome() {
		return this.getFieldType() == LudoFieldType.HOME;
	}
	
	@Override
	public boolean isStart() {
		return this.getFieldPos().isStart();
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return figure.getStartPos().equals(this.getFieldPos());
	}
	
	@Override
	public boolean isWin() {
		return this.getFieldType() == LudoFieldType.WIN;
	}
	
	@Override
	public ImageView getFieldBackground() {
		if (this.getFieldType() == LudoFieldType.DEFAULT && this.getColorType() == LudoPlayerType.NO) {
			return this.makeImage("textures/ludo/field/field.png");
		} else if (this.getColorType() != LudoPlayerType.NO) {
			if (this.getColorType() instanceof LudoPlayerType playerType) {
				return switch (playerType) {
					case LudoPlayerType.GREEN -> this.makeImage("textures/ludo/field/green_field.png");
					case LudoPlayerType.YELLOW -> this.makeImage("textures/ludo/field/yellow_field.png");
					case LudoPlayerType.BLUE -> this.makeImage("textures/ludo/field/blue_field.png");
					case LudoPlayerType.RED -> this.makeImage("textures/ludo/field/red_field.png");
					default -> {
						GameField.LOGGER.warn("Fail to get field background for field {} with type {} and color type {}", this.getFieldPos().getPosition(), this.getFieldType(), this.getColorType());
						yield null;
					}
				};
			} else {
				throw new ClassCastException();
			}
		}
		GameField.LOGGER.warn("Fail to get field background for field {} with type {} and color type {}", this.getFieldPos().getPosition(), this.getFieldType(), this.getColorType());
		return null;
	}
	
	@Override
	public final GameResult getResult() {
		GameField.LOGGER.warn("Fail to get the game result of field {}, since ludo fields does not have a game result", this.getFieldPos().getPosition());
		return GameResult.NO;
	}
	
	@Override
	public final void setResult(GameResult result) {
		GameField.LOGGER.warn("Fail to set the game result of field {}, since ludo fields do not store the game result", this.getFieldPos().getPosition());
	}
	
	@Override
	public boolean canSelect() {
		return !this.isEmpty();
	}
	
	@Override
	public void updateFieldGraphic() {
		Box<Node> fieldBackground = new Box<>(this.getFieldBackground());
		if (this.isEmpty()) {
			StackPane pane = new StackPane(fieldBackground);
			if (this.isShadowed()) {
				pane.getChildren().add(new Box<>(this.makeImage("textures/ludo/figure/figure_shadow.png", 0.95)));
			}
			this.setGraphic(pane);
		} else {
			assert this.getFigure() != null;
			StackPane pane = new StackPane(fieldBackground, new Box<>(this.getFigure().getPlayerType().getImage(this.getSize() * 0.95, this.getSize() * 0.95)));
			if (this.isShadowed()) {
				pane.getChildren().add(new Box<>(this.makeImage("textures/ludo/figure/figure_shadow.png", 0.95)));
			}
			this.setGraphic(pane);
		}
	}
	
	
	@Override
	public String toString() {
		return ToString.toString(this, "result", "button", "group");
	}
	
}
