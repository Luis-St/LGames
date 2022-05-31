package net.vgc.client.game.games.ludo.map.field;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.vgc.Constans;
import net.vgc.client.fx.Box;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.game.games.ludo.player.figure.LudoClientFigure;
import net.vgc.client.game.map.field.ClientGameField;
import net.vgc.client.game.map.field.FieldRenderState;
import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;

public class LudoClientField extends ToggleButton implements ClientGameField, PacketHandler<ClientPacket> {
	
	protected final ToggleGroup group;
	protected final LudoFieldType fieldType;
	protected final LudoPlayerType colorType;
	protected final LudoFieldPos fieldPos;
	protected final double imageSize;
	protected LudoClientFigure figure = null;
	protected LudoFieldRenderState renderState = LudoFieldRenderState.NO;
	protected boolean shadowed = false;
	
	public LudoClientField(ToggleGroup group, LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos, double imageSize) {
		this.group = group;
		this.fieldType = fieldType;
		this.colorType = colorType;
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
	public LudoFieldType getFieldType() {
		return this.fieldType;
	}
	
	public LudoPlayerType getColorType() {
		return this.colorType;
	}

	@Override
	public LudoFieldPos getFieldPos() {
		return this.fieldPos;
	}

	@Override
	public boolean isHome() {
		return this.fieldType == LudoFieldType.HOME;
	}

	@Override
	public boolean isStart() {
		return this.fieldPos.isStart();
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return figure.getStartPos().equals(this.fieldPos);
	}
	
	@Override
	public boolean isWin() {
		return this.fieldType == LudoFieldType.WIN;
	}

	@Override
	public LudoClientFigure getFigure() {
		return this.figure;
	}
	
	@Override
	public ImageView getFieldBackground() {
		if (this.fieldType == LudoFieldType.DEFAULT && this.colorType == LudoPlayerType.NO) {
			return this.makeImage("textures/ludo/field/field.png");
		} else if (this.fieldType != LudoFieldType.DEFAULT && this.colorType != LudoPlayerType.NO) {
			switch (this.colorType) {
				case GREEN: return this.makeImage("textures/ludo/field/green_field.png");
				case YELLOW: return this.makeImage("textures/ludo/field/yellow_field.png");
				case BLUE: return this.makeImage("textures/ludo/field/blue_field.png");
				case RED: return this.makeImage("textures/ludo/field/red_field.png");
				default: break;
			}
		}
		LOGGER.warn("Fail to get field background for field {} with type {} and color type {}", this.fieldPos.getPosition(), this.fieldType, this.colorType);
		return null;
	}
	
	protected ImageView makeImage(String path) {
		return FxUtil.makeImageView(path, this.imageSize * 0.95, this.imageSize * 0.95);
	}
	
	@Override
	public LudoFieldRenderState getRenderState() {
		return this.renderState;
	}
	
	@Override
	public void setRenderState(FieldRenderState renderState) {
		this.renderState = (LudoFieldRenderState) renderState;
		this.updateFieldGraphic();
	}
	
	@Override
	public boolean canSelectField() {
		return !this.isEmpty() && this.renderState == LudoFieldRenderState.DEFAULT;
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
	public void updateFieldGraphic() {
		Box<Node> fieldBackground = new Box<>(this.getFieldBackground());
		if (this.isEmpty()) {
			StackPane pane = new StackPane(fieldBackground);
			if (this.isShadowed()) {
				pane.getChildren().add(new Box<>(FxUtil.makeImageView("textures/ludo/figure/figure_shadow.png", this.imageSize * 0.95, this.imageSize * 0.95)));
			}
			this.setGraphic(pane);
		} else if (this.renderState == LudoFieldRenderState.DEFAULT) {
			StackPane pane = new StackPane(fieldBackground, new Box<>(this.getFigure().getPlayerType().getImage(this.imageSize * 0.95, this.imageSize * 0.95)));
			if (this.isShadowed()) {
				pane.getChildren().add(new Box<>(FxUtil.makeImageView("textures/ludo/figure/figure_shadow.png", this.imageSize * 0.95, this.imageSize * 0.95)));
			}
			this.setGraphic(pane);
		} else {
			LOGGER.warn("Fail to update field graphic of field {} with type {}, since the field has a figure on it but the render type is set to {}", this.fieldPos.getPosition(), this.fieldType, this.renderState);
			this.setGraphic(new StackPane(fieldBackground));
		}
	}
	
	@Override
	public void handlePacket(ClientPacket packet) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoClientField field) {
			if (!this.fieldType.equals(field.fieldType)) {
				return false;
			} else if (!this.colorType.equals(field.colorType)) {
				return false;
			} else if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else if (!Objects.equals(this.figure, field.figure)) {
				return false;
			} else if (!Objects.equals(this.renderState, field.renderState)) {
				return false;
			} else {
				return this.shadowed == field.shadowed;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoClientField{");
		builder.append("fieldType=").append(this.fieldType).append(",");
		builder.append("colorType=").append(this.colorType).append(",");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append(",");
		builder.append("renderState=").append(this.renderState).append("}");
		return builder.toString();
	}

}