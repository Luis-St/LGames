package net.vgc.client.game.games.wins4.map.field;

import java.util.Objects;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.vgc.client.Client;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.game.games.wins4.Wins4ClientGame;
import net.vgc.client.game.games.wins4.player.figure.Wins4ClientFigure;
import net.vgc.client.game.map.field.ClientGameField;
import net.vgc.client.game.map.field.FieldRenderState;
import net.vgc.game.GameResult;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.map.field.Wins4FieldType;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.player.field.GameFigure;
import net.vgc.player.GameProfile;
import net.vgc.util.Util;

public class Wins4ClientField extends Label implements ClientGameField {
	
	protected final Client client;
	protected final Wins4ClientGame game;
	protected final Wins4FieldPos fieldPos;
	protected final double imageSize;
	protected Wins4ClientFigure figure;
	protected Wins4FieldRenderState renderState = Wins4FieldRenderState.NO;
	protected boolean shadowed = false;
	protected GameResult result = GameResult.NO;
	
	public Wins4ClientField(Client client, Wins4ClientGame game, Wins4FieldPos fieldPos, double imageSize) {
		this.client = client;
		this.game = game;
		this.fieldPos = fieldPos;
		this.imageSize = imageSize;
		this.init();
	}

	@Override
	public void init() {
		this.setPrefSize(this.imageSize, this.imageSize);
		this.setFocusTraversable(false);
		this.updateFieldGraphic();
	}

	@Override
	public Wins4FieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since the 4 wins fields does not have a field type", this.getFieldPos().getPosition());
		return Wins4FieldType.DEFAULT;
	}

	@Override
	public Wins4FieldPos getFieldPos() {
		return this.fieldPos;
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
	public Wins4ClientFigure getFigure() {
		return this.figure;
	}

	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (Wins4ClientFigure) figure;
		this.updateFieldGraphic();
	}
	
	@Override
	public void clear() {
		ClientGameField.super.clear();
		if (this.renderState != Wins4FieldRenderState.SHADOW) {
			this.renderState = Wins4FieldRenderState.NO;
		}
	}
	
	@Override
	public ImageView getFieldBackground() {
		return switch (this.result) {
			case WIN -> FxUtil.makeImageView("textures/wins4/field/field_background_win.png", this.imageSize, this.imageSize); 
			case LOSE -> FxUtil.makeImageView("textures/wins4/field/field_background_lose.png", this.imageSize, this.imageSize);
			case DRAW -> FxUtil.makeImageView("textures/wins4/field/field_background_draw.png", this.imageSize, this.imageSize);
			default -> FxUtil.makeImageView("textures/wins4/field/field_background.png", this.imageSize, this.imageSize);
		};
	}

	@Override
	@Deprecated
	public Wins4FieldRenderState getRenderState() {
		return this.renderState;
	}

	@Override
	@Deprecated
	public void setRenderState(FieldRenderState renderState) {
		this.renderState = (Wins4FieldRenderState) renderState;
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
	
	public GameResult getResult() {
		return this.result;
	}
	
	public void setResult(GameResult result) {
		this.result = result;
		this.updateFieldGraphic();
	}

	@Override
	public void updateFieldGraphic() {
		ImageView fieldBackground = this.getFieldBackground();
		StackPane pane = new StackPane(fieldBackground);
		if (this.isEmpty()) {
			if (this.isShadowed()) {
				pane.getChildren().add(FxUtil.makeImageView("textures/wins4/figure/figure_shadow.png", this.imageSize, this.imageSize));
			}
			this.setGraphic(pane);
		} else {
			ImageView figure = this.figure.getPlayer().getPlayerType().getImage(null, this.imageSize, this.imageSize);
			if (figure != null) {
				pane.getChildren().add(figure);
			}
			if (this.isShadowed()) {
				pane.getChildren().add(FxUtil.makeImageView("textures/wins4/figure_shadow.png", this.imageSize, this.imageSize));
			}
			this.setGraphic(pane);
		}
	}
	
	@Override
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(this.getFieldType(), Wins4PlayerType.NO, this.fieldPos, GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		Wins4ClientFigure figure = this.getFigure();
		return new GameFieldInfo(this.getFieldType(), Wins4PlayerType.NO, this.fieldPos, figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ClientField field) {
			if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else if (!Objects.equals(this.figure, field.figure)) {
				return false;
			} else {
				return Objects.equals(this.renderState, field.renderState);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Win4ClientField{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append(",");
		builder.append("renderState=").append(this.renderState).append("}");
		return builder.toString();
	}

}
