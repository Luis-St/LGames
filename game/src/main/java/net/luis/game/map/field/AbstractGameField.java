package net.luis.game.map.field;

import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameField implements GameField {
	
	protected final double fieldSize;
	private final Game game;
	private final GameFieldType fieldType;
	private final GamePlayerType colorType;
	private final GameFieldPos fieldPos;
	private GameFigure figure;
	private GameResult result = GameResult.NO;
	private boolean shadowed = false;
	
	protected AbstractGameField(@NotNull Game game, @NotNull GameFieldType fieldType, @NotNull GamePlayerType colorType, @NotNull GameFieldPos fieldPos, double fieldSize) {
		this.game = game;
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
	public @NotNull Game getGame() {
		return this.game;
	}
	
	@Override
	public @NotNull GameFieldType getFieldType() {
		return this.fieldType;
	}
	
	@Override
	public @NotNull GamePlayerType getColorType() {
		return this.colorType;
	}
	
	@Override
	public @NotNull GameFieldPos getFieldPos() {
		return this.fieldPos;
	}
	
	@Override
	public @Nullable GameFigure getFigure() {
		return this.figure;
	}
	
	@Override
	public void setFigure(@Nullable GameFigure figure) {
		this.figure = figure;
		this.updateFieldGraphic();
	}
	
	@Override
	public @NotNull GameResult getResult() {
		return this.result;
	}
	
	@Override
	public void setResult(@NotNull GameResult result) {
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
	
	protected ImageView makeImage(@NotNull String path) {
		return this.makeImage(path, 1.0);
	}
	
	protected ImageView makeImage(@NotNull String path, double scale) {
		return FxUtils.makeImageView(FxApplication.getInstance().getResourceManager().resourceDirectory().resolve(path).toString(), this.fieldSize * scale, this.fieldSize * scale);
	}
	
	//region Object overrides
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGameField that)) return false;
		
		if (Double.compare(that.fieldSize, this.fieldSize) != 0) return false;
		if (this.shadowed != that.shadowed) return false;
		if (!this.fieldType.equals(that.fieldType)) return false;
		if (!this.colorType.equals(that.colorType)) return false;
		if (!this.fieldPos.equals(that.fieldPos)) return false;
		if (!Objects.equals(this.figure, that.figure)) return false;
		return this.result == that.result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.fieldType, this.colorType, this.fieldPos, this.fieldSize, this.figure, this.result, this.shadowed);
	}
	//endregion
}
