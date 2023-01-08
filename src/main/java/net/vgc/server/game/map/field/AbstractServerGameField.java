package net.vgc.server.game.map.field;

import javafx.scene.image.ImageView;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.AbstractGameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractServerGameField extends AbstractGameField {
	
	protected AbstractServerGameField(GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos) {
		super(map, fieldType, colorType, fieldPos);
	}
	
	@Override
	public final void init() {
		LOGGER.warn("Can not initialize the client field settings on server");
	}
	
	@Override
	public final boolean canSelect() {
		LOGGER.warn("Can not select the field on server");
		return false;
	}
	
	@Override
	public final boolean isShadowed() {
		LOGGER.warn("Can not get the shadow value on server");
		return false;
	}
	
	@Override
	public final void setShadowed(boolean shadowed) {
		LOGGER.warn("Can not set the shadow value on server");
	}
	
	@Nullable
	@Override
	public final ImageView getFieldBackground() {
		LOGGER.warn("Can not get the field background on server");
		return null;
	}
	
	@Override
	public final void updateFieldGraphic() {
		LOGGER.warn("Can not update the field graphic on server");
	}
	
}
