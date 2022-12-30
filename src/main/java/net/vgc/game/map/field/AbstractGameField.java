package net.vgc.game.map.field;

import net.vgc.game.GameResult;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameField implements GameField {
	
	private final GameMap map;
	private final GameFieldType fieldType;
	private final GamePlayerType colorType;
	private final GameFieldPos fieldPos;
	private GameFigure figure;
	private GameResult result = GameResult.NO;
	
	protected AbstractGameField(GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos) {
		this.map = map;
		this.fieldType = fieldType;
		this.colorType = colorType;
		this.fieldPos = fieldPos;
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
	}
	
	@Override
	public GameResult getResult() {
		return this.result;
	}
	
	@Override
	public void setResult(GameResult result) {
		this.result = result;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof AbstractGameField field) {
			if (!this.fieldType.equals(field.fieldType)) {
				return false;
			} else if (!this.colorType.equals(field.colorType)) {
				return false;
			} else if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else if (!Objects.equals(this.figure, field.figure)) {
				return false;
			} else {
				return Objects.equals(this.result, field.result);
			}
		}
		return false;
	}
	
}
