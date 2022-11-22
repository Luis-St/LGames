package net.vgc.client.games.ttt.map.field;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.game.wrapper.ToggleButtonWrapper;
import net.vgc.client.game.map.field.AbstractClientGameField;
import net.vgc.game.GameResult;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ttt.map.field.TTTFieldType;
import net.vgc.games.ttt.player.TTTPlayerType;
import net.vgc.util.ToString;

public class TTTClientField extends AbstractClientGameField implements ToggleButtonWrapper {
	
	private final ToggleButton button;
	private final ToggleGroup group;
	
	public TTTClientField(Client client, GameMap map, ToggleGroup group, GameFieldPos fieldPos, double size) {
		super(client, map, TTTFieldType.DEFAULT, TTTPlayerType.NO, fieldPos, size);
		this.button = new ToggleButton();
		this.group = group;
		this.init();
	}
	
	@Override
	public void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.getSize(), this.getSize());
		this.setFocusTraversable(false);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}
	
	@Override
	public ToggleButton getToggleButton() {
		return this.button;
	}
	
	@Override
	public final GameFieldType getFieldType() {
		LOGGER.warn("Fail to get the field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return super.getFieldType();
	}
	
	@Override
	public final GamePlayerType getColorType() {
		LOGGER.warn("Fail to get field color type of field {}, since tic tac toe fields does not have a field color type", this.getFieldPos().getPosition());
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
		return null;
	}
	
	@Override
	public boolean canSelect() {
		return this.isEmpty() && this.getResult() == GameResult.NO;
	}
	
	@Override
	public void updateFieldGraphic() {
		if (this.isEmpty()) {
			if (this.isShadowed()) {
				if (this.getResult() == GameResult.NO) {
					if (this.getMap().getGame().getPlayerFor(this.getClient().getPlayer()).getPlayerType() instanceof TTTPlayerType playerType) {
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
	
	protected ImageView getFigureTexture() {
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
	
	@Override
	public String toString() {
		return ToString.toString(this, "fieldType", "colorType", "result");
	}
	
}
