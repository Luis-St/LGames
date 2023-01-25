package net.luis.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.map.GameMap;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.GameProfile;
import net.luis.game.player.figure.GameFigure;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public interface GameField {
	
	Logger LOGGER = LogManager.getLogger();
	
	void init();
	
	GameMap getMap();
	
	GameFieldType getFieldType();
	
	GamePlayerType getColorType();
	
	GameFieldPos getFieldPos();
	
	boolean isHome();
	
	boolean isStart();
	
	boolean isStartFor(GameFigure figure);
	
	boolean isWin();
	
	default boolean isSpecial() {
		return this.isHome() || this.isStart() || this.isWin();
	}
	
	@Nullable
	GameFigure getFigure();
	
	void setFigure(@Nullable GameFigure figure);
	
	default boolean isEmpty() {
		return this.getFigure() == null;
	}
	
	@Deprecated
	default void clearFigure() {
		this.setFigure(null);
	}
	
	GameResult getResult();
	
	void setResult(GameResult result);
	
	default boolean canSelect() {
		return ApplicationType.CLIENT.isOn();
	}
	
	boolean isShadowed();
	
	void setShadowed(boolean shadowed);
	
	default void clear() {
		this.setFigure(null);
		this.setShadowed(false);
	}
	
	@Nullable
	default ImageView getFieldBackground() {
		return null;
	}
	
	void updateFieldGraphic();
	
	default GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.getFieldPos(), GameProfile.EMPTY, -1, Utils.EMPTY_UUID);
		}
		GameFigure figure = this.getFigure();
		assert figure != null;
		return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.getFieldPos(), figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
}
