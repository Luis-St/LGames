package net.vgc.client.game.map.field;

import javax.annotation.Nullable;

import javafx.scene.image.ImageView;
import net.vgc.client.game.player.figure.ClientGameFigure;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public interface ClientGameField extends GameField, PacketHandler<ClientPacket> {
	
	void init();
	
	@Override
	ClientGameFigure getFigure();
	
	@Override
	default void setFigure(GameFigure figure) {
		LOGGER.warn("Can not set the figure of field {} with type {} on client", this.getFieldPos().getPosition(), this.getFieldType());
	}
	
	@Override
	default void clear() {
		LOGGER.warn("Can not clear the field {} with type {} on client", this.getFieldPos().getPosition(), this.getFieldType());
	}
	
	@Nullable
	ImageView getFieldBackground();
	
	FieldRenderState getRenderState();
	
	void setRenderState(FieldRenderState renderState);
	
	boolean canSelectField();
	
	boolean isShadowed();
	
	void setShadowed(boolean shadowed);
	
	void updateFieldGraphic();
	
}
