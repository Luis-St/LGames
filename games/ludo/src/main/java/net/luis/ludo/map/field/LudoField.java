package net.luis.ludo.map.field;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.luis.Constants;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.Game;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ludo.LudoGame;
import net.luis.ludo.player.LudoPlayerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class LudoField extends AbstractGameField implements ToggleButtonWrapper {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoGame.class);
	
	private final ToggleButton button = new ToggleButton();
	private final ToggleGroup group;
	
	public LudoField(Game game, ToggleGroup group, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos, double fieldSize) {
		super(game, fieldType, colorType, fieldPos, fieldSize);
		this.group = group;
	}
	
	@Override
	public @NotNull ToggleButton getToggleButton() {
		this.button.setUserData(this);
		return this.button;
	}
	
	@Override
	public void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.fieldSize, this.fieldSize);
		this.setFocusTraversable(false);
		if (!Constants.DEBUG_MODE) {
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
	public boolean isStartFor(@NotNull GameFigure figure) {
		return figure.getStartPos().equals(this.getFieldPos());
	}
	
	@Override
	public boolean isWin() {
		return this.getFieldType() == LudoFieldType.WIN;
	}
	
	@Override
	public @Nullable ImageView getFieldBackground() {
		if (this.getFieldType() == LudoFieldType.DEFAULT && this.getColorType() == LudoPlayerType.NO) {
			return this.makeImage("textures/ludo/field/field.png");
		} else if (this.getColorType() != LudoPlayerType.NO) {
			return switch ((LudoPlayerType) this.getColorType()) {
				case GREEN -> this.makeImage("textures/ludo/field/green_field.png");
				case YELLOW -> this.makeImage("textures/ludo/field/yellow_field.png");
				case BLUE -> this.makeImage("textures/ludo/field/blue_field.png");
				case RED -> this.makeImage("textures/ludo/field/red_field.png");
				default -> {
					LOGGER.warn("Fail to get field background for field {} with type {} and color type {}", this.getFieldPos().getPosition(), this.getFieldType(), this.getColorType());
					yield null;
				}
			};
		}
		LOGGER.warn("Fail to get field background for field {} with type {} and color type {}", this.getFieldPos().getPosition(), this.getFieldType(), this.getColorType());
		return null;
	}
	
	@Override
	public void updateFieldGraphic() {
		VBox fieldBackground = new VBox(this.getFieldBackground());
		if (this.isEmpty()) {
			StackPane pane = new StackPane(fieldBackground);
			if (this.isShadowed()) {
				pane.getChildren().add(new VBox(this.makeImage("textures/ludo/figure/figure_shadow.png", 0.95)));
			}
			this.setGraphic(pane);
		} else {
			assert this.getFigure() != null;
			StackPane pane = new StackPane(fieldBackground, new VBox(this.getFigure().getPlayerType().getImage(this.fieldSize * 0.95, this.fieldSize * 0.95)));
			if (this.isShadowed()) {
				pane.getChildren().add(new VBox(this.makeImage("textures/ludo/figure/figure_shadow.png", 0.95)));
			}
			this.setGraphic(pane);
		}
	}
	
}
