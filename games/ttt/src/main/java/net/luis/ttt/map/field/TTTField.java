package net.luis.ttt.map.field;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.luis.Constants;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ttt.player.TTTPlayerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class TTTField extends AbstractGameField implements ToggleButtonWrapper {
	
	private static final Logger LOGGER = LogManager.getLogger(TTTField.class);
	
	private final ToggleButton button = new ToggleButton();
	private final ToggleGroup group;
	
	public TTTField(Game game, ToggleGroup group, GameFieldPos fieldPos) {
		super(game, TTTFieldType.DEFAULT, TTTPlayerType.NO, fieldPos, 150.0);
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
		return false;
	}
	
	@Override
	public boolean isStart() {
		return false;
	}
	
	@Override
	public boolean isStartFor(@NotNull GameFigure figure) {
		return false;
	}
	
	@Override
	public boolean isWin() {
		return false;
	}
	
	@Override
	public boolean canSelect() {
		return this.isEmpty() && this.getResult() == GameResult.NO;
	}
	
	@Override
	public void updateFieldGraphic() {
		if (ApplicationType.CLIENT.isOn()) {
			if (this.isEmpty()) {
				if (this.isShadowed()) {
					if (this.getResult() == GameResult.NO) {
						// this.setGraphic(Objects.requireNonNull(this.getGame().getPlayerFor(profile)).getPlayerType().getImage("_shadow", this.fieldSize * 0.95, this.fieldSize * 0.95)); // TODO: fix
					} else {
						this.setShadowed(false);
						this.updateFieldGraphic();
					}
				} else {
					this.setGraphic(null);
				}
			} else {
				if (this.isShadowed()) {
					this.setShadowed(false);
					LOGGER.warn("Can not display a shadow figure on a non empty field");
					this.updateFieldGraphic();
				} else {
					this.setGraphic(this.getFigureTexture());
				}
			}
		}
		
	}
	
	private @Nullable ImageView getFigureTexture() {
		GamePlayerType playerType = Objects.requireNonNull(this.getFigure()).getPlayerType();
		return playerType.getImage("_" + this.getResult().getName(), this.fieldSize * 0.95, this.fieldSize * 0.95);
	}
	
}
