package net.vgc.client.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.vgc.client.Client;
import net.vgc.game.GameResult;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.AbstractGameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.network.Network;

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
		return FxUtils.makeImageView(Network.INSTANCE.getResourceDirectory().resolve(path).toString(), this.getSize() * scale, this.getSize() * scale);
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
