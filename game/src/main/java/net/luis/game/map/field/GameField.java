package net.luis.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.map.GameMap;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public interface GameField {
	
	void init();
	
	@NotNull Game getGame();
	
	default @NotNull GameMap getMap() {
		return this.getGame().getMap();
	}
	
	@NotNull GameFieldType getFieldType();
	
	@NotNull GamePlayerType getColorType();
	
	@NotNull GameFieldPos getFieldPos();
	
	boolean isHome();
	
	boolean isStart();
	
	boolean isStartFor(@NotNull GameFigure figure);
	
	boolean isWin();
	
	default boolean isSpecial() {
		return this.isHome() || this.isStart() || this.isWin();
	}
	
	GameFigure getFigure();
	
	void setFigure(@Nullable GameFigure figure);
	
	default boolean isEmpty() {
		return this.getFigure() == null;
	}
	
	@Deprecated
	default void clearFigure() {
		this.setFigure(null);
	}
	
	@NotNull GameResult getResult();
	
	void setResult(@NotNull GameResult result);
	
	default boolean canSelect() {
		return !this.isEmpty();
	}
	
	boolean isShadowed();
	
	void setShadowed(boolean shadowed);
	
	default void clear() {
		this.setFigure(null);
		this.setShadowed(false);
	}
	
	default ImageView getFieldBackground() {
		return null;
	}
	
	void updateFieldGraphic();
	
	default @NotNull GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.getFieldPos(), GameProfile.EMPTY, -1, Utils.EMPTY_UUID);
		}
		GameFigure figure = this.getFigure();
		assert figure != null;
		return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.getFieldPos(), figure.getPlayer().getPlayer().getProfile(), figure.getIndex(), figure.getUniqueId());
	}
}
