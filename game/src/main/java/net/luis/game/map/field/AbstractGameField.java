package net.luis.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.GameApplication;
import net.luis.game.map.GameMap;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameField implements GameField {
	
	protected final double fieldSize;
	private final GameMap map;
	private final GameFieldType fieldType;
	private final GamePlayerType colorType;
	private final GameFieldPos fieldPos;
	private GameFigure figure;
	private GameResult result = GameResult.NO;
	private boolean shadowed = false;
	
	protected AbstractGameField(GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos, double fieldSize) {
		this.map = map;
		this.fieldType = fieldType;
		this.colorType = colorType;
		this.fieldPos = fieldPos;
		this.fieldSize = fieldSize;
		this.init();
	}
	
	@Override
	public void init() {
	
	}
	
	@Override
	public GameMap getMap() {
		return this.map;
	}
	
	@Override
	public GameFieldType getFieldType() {
		return this.fieldType;
	}
	
	@Override
	public GamePlayerType getColorType() {
		return this.colorType;
	}
	
	@Override
	public GameFieldPos getFieldPos() {
		return this.fieldPos;
	}
	
	@Override
	public GameFigure getFigure() {
		return this.figure;
	}
	
	@Override
	public void setFigure(GameFigure figure) {
		this.figure = figure;
		this.updateFieldGraphic();
	}
	
	@Override
	public GameResult getResult() {
		return this.result;
	}
	
	@Override
	public void setResult(GameResult result) {
		this.result = result;
		this.updateFieldGraphic();
	}
	
	@Override
	public boolean isShadowed() {
		return ApplicationType.CLIENT.isOn() && this.shadowed;
	}
	
	@Override
	public void setShadowed(boolean shadowed) {
		this.shadowed = ApplicationType.CLIENT.isOn() && shadowed;
		this.updateFieldGraphic();
	}
	
	@Override
	public void updateFieldGraphic() {
	
	}
	
	protected ImageView makeImage(String path) {
		return this.makeImage(path, 1.0);
	}
	
	protected ImageView makeImage(String path, double scale) {
		return FxUtils.makeImageView(Objects.requireNonNull(GameApplication.getInstance()).getResourceDirectory().resolve(path).toString(), this.fieldSize * scale, this.fieldSize * scale);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGameField that)) return false;
		
		if (Double.compare(that.fieldSize, this.fieldSize) != 0) return false;
		if (this.shadowed != that.shadowed) return false;
		if (!this.map.equals(that.map)) return false;
		if (!this.fieldType.equals(that.fieldType)) return false;
		if (!this.colorType.equals(that.colorType)) return false;
		if (!this.fieldPos.equals(that.fieldPos)) return false;
		if (!Objects.equals(this.figure, that.figure)) return false;
		return this.result == that.result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.map, this.fieldType, this.colorType, this.fieldPos, this.fieldSize, this.figure, this.result, this.shadowed);
	}
}
