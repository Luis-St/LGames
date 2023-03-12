package net.luis.ttt.map.field;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.luis.Constants;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ttt.player.TTTPlayerType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class TTTField extends AbstractGameField implements ToggleButtonWrapper {
	
	private final ToggleButton button = new ToggleButton();
	private final ToggleGroup group;
	
	public TTTField(GameMap map, ToggleGroup group, GameFieldPos fieldPos) {
		super(map, TTTFieldType.DEFAULT, TTTPlayerType.NO, fieldPos, 150.0);
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
						GameProfile profile = Objects.requireNonNull(FxApplication.getInstance()).getGameManager().getLocalProfile();
						if (Objects.requireNonNull(this.getMap().getGame().getPlayerFor(profile)).getPlayerType() instanceof TTTPlayerType playerType) {
							this.setGraphic(this.makeImage(playerType.getPath() + "_shadow.png", 0.95));
						} else {
							throw new ClassCastException();
						}
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
	
	private ImageView getFigureTexture() {
		if (this.getFigure().getPlayerType() instanceof TTTPlayerType playerType) {
			return switch (this.getResult()) {
				case WIN -> this.makeImage(playerType.getPath() + "_win.png", 0.95);
				case LOSE -> this.makeImage(playerType.getPath() + "_lose.png", 0.95);
				case DRAW -> this.makeImage(playerType.getPath() + "_draw.png", 0.95);
				default -> this.makeImage(playerType.getPath() + ".png", 0.95);
			};
		}
		throw new ClassCastException();
	}
	
}
