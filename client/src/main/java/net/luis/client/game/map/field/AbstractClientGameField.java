package net.luis.client.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.application.GameApplication;
import net.luis.client.Client;
import net.luis.fxutils.FxUtils;
import net.luis.game.GameResult;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractClientGameField extends AbstractGameField {
	
	private final Client client;
	private final double size;
	private boolean shadowed = false;
	
	protected AbstractClientGameField(Client client, GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos, double size) {
		super(map, fieldType, colorType, fieldPos);
		this.client = client;
		this.size = size;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	protected double getSize() {
		return this.size;
	}
	
	protected ImageView makeImage(String path) {
		return this.makeImage(path, 1.0);
	}
	
	protected ImageView makeImage(String path, double scale) {
		return FxUtils.makeImageView(Objects.requireNonNull(GameApplication.getInstance()).getResourceDirectory().resolve(path).toString(), this.getSize() * scale, this.getSize() * scale);
	}
	
	@Override
	public void setFigure(GameFigure figure) {
		super.setFigure(figure);
		this.updateFieldGraphic();
	}
	
	@Override
	public void setResult(GameResult result) {
		super.setResult(result);
		this.updateFieldGraphic();
	}
	
	@Override
	public boolean isShadowed() {
		return this.shadowed;
	}
	
	@Override
	public void setShadowed(boolean shadowed) {
		this.shadowed = shadowed;
		this.updateFieldGraphic();
	}
	
	@Override
	public void clear() {
		this.clearFigure();
		this.clearShadow();
	}
	
}
