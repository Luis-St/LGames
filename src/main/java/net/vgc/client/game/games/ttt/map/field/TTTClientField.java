package net.vgc.client.game.games.ttt.map.field;

import java.util.Objects;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.client.game.games.ttt.player.figure.TTTClientFigure;
import net.vgc.client.game.map.field.ClientGameField;
import net.vgc.client.game.map.field.FieldRenderState;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.map.field.TTTFieldType;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.client.ClientPacket;

public class TTTClientField extends ToggleButton implements ClientGameField {
	
	protected final Client client;
	protected final TTTClientGame game;
	protected final ToggleGroup group;
	protected final TTTFieldPos fieldPos;
	protected final double imageSize;
	protected TTTClientFigure figure;
	protected TTTFieldRenderState renderState = TTTFieldRenderState.NO;
	
	public TTTClientField(Client client, TTTClientGame game, ToggleGroup group, TTTFieldPos fieldPos, double imageSize) {
		this.client = client;
		this.game = game;
		this.group = group;
		this.fieldPos = fieldPos;
		this.imageSize = imageSize;
		this.init();
	}
	
	@Override
	public void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.imageSize, this.imageSize);
		this.setFocusTraversable(false);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
		this.updateFieldGraphic();
	}

	@Override
	public TTTFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return TTTFieldType.DEFAULT;
	}

	@Override
	public TTTFieldPos getFieldPos() {
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
	public TTTClientFigure getFigure() {
		return this.figure;
	}
	
	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (TTTClientFigure) figure;
	}
	
	@Override
	public void clear() {
		ClientGameField.super.clear();
		if (this.renderState != TTTFieldRenderState.SHADOW) {
			this.renderState = TTTFieldRenderState.NO;
		}
	}

	@Override
	public ImageView getFieldBackground() {
		return null;
	}

	@Override
	public TTTFieldRenderState getRenderState() {
		return this.renderState;
	}

	@Override
	public void setRenderState(FieldRenderState renderState) {
		this.renderState = (TTTFieldRenderState) renderState;
		this.updateFieldGraphic();
	}

	@Override
	public boolean canSelectField() {
		return this.figure == null;
	}

	@Override
	public boolean isShadowed() {
		return this.renderState == TTTFieldRenderState.SHADOW;
	}

	@Override
	public void setShadowed(boolean shadowed) {
		if (shadowed) {
			if (this.renderState == TTTFieldRenderState.NO) {
				this.renderState = TTTFieldRenderState.SHADOW;
				this.updateFieldGraphic();
			} else if (this.renderState == TTTFieldRenderState.SHADOW) {
				LOGGER.info("Field {} is already shadowed", this.fieldPos.getPosition());
			} else {
				LOGGER.warn("Fail to shadow field {}, since the current render state is {}", this.renderState);
			}
		} else {
			if (this.renderState == TTTFieldRenderState.NO) {
				LOGGER.info("Field {} is already unshadowed", this.fieldPos.getPosition());
			} else if (this.renderState == TTTFieldRenderState.SHADOW) {
				this.renderState = TTTFieldRenderState.NO;
				this.updateFieldGraphic();
			} else {
				LOGGER.warn("Fail to shadow field {}, since the current render state is {}", this.renderState);
			}
		}
	}

	@Override
	public void updateFieldGraphic() {
		TTTPlayerType playerType = (TTTPlayerType) this.game.getPlayerFor(this.client.getPlayer()).getPlayerType();
		if (this.isEmpty()) {
			if (!this.renderState.canRenderWithFigure()) {
				this.setGraphic(playerType.getImage(this.renderState, this.imageSize * 0.95, this.imageSize * 0.95));
			} else {
				this.setGraphic(null);
			}
		} else {
			if (this.renderState.canRenderWithFigure()) {
				this.setGraphic(this.figure.getPlayerType().getImage(this.renderState, this.imageSize * 0.95, this.imageSize * 0.95));
			} else {
				LOGGER.warn("Fail to display figure {} of player {} on field {}, since the render state is {}", this.figure.getCount(), this.figure.getPlayer().getPlayer().getProfile().getName(), this.fieldPos.getPosition(), this.renderState);
				this.setGraphic(null);
			}
		}
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTClientField field) {
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
		StringBuilder builder = new StringBuilder("TTTClientField{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append(",");
		builder.append("renderState=").append(this.renderState).append("}");
		return builder.toString();
	}

}
