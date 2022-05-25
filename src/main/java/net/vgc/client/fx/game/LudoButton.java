package net.vgc.client.fx.game;

import javax.annotation.Nullable;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.vgc.client.Client;
import net.vgc.client.fx.Box;
import net.vgc.client.fx.FxUtil;
import net.vgc.oldgame.ludo.LudoState;
import net.vgc.oldgame.ludo.LudoType;
import net.vgc.oldgame.ludo.map.field.LudoFieldPos;
import net.vgc.oldgame.ludo.map.field.LudoFieldType;

public class LudoButton extends ToggleButton {
	
	protected final Client client;
	protected final ToggleGroup group;
	protected final ImageView fieldImage;
	protected final LudoFieldType fieldType;
	protected final LudoFieldPos pos;
	protected final double prefSize;
	protected LudoType type;
	protected LudoType lastType = LudoType.NO;
	protected LudoState state;
	protected LudoState lastState = LudoState.DEFAULT;
	
	public LudoButton(Client client, ToggleGroup group, String fieldPath, LudoFieldType fieldType, LudoFieldPos pos, double prefSize) {
		this.client = client;
		this.group = group;
		this.fieldImage = FxUtil.makeImageView(fieldPath, prefSize * 0.95, prefSize * 0.95);
		this.fieldType = fieldType;
		this.pos = pos;
		this.prefSize = prefSize;
		this.init();
	}
	
	protected void init() {
		this.setToggleGroup(this.group);
		this.setPrefSize(this.prefSize, this.prefSize);
		this.setFocusTraversable(false);
//		if (!Constans.DEBUG) {
//			this.setBackground(null);
//		}
		this.updateGraphic(null);
	}
	
	protected void updateGraphic(@Nullable ImageView image) {
		if (image != null) {
			this.setGraphic(new StackPane(new Box<>(this.fieldImage, Pos.CENTER), new Box<>(image, Pos.CENTER)));
		} else {
			this.setGraphic(new StackPane(new Box<>(this.fieldImage, Pos.CENTER)));
		}
	}
	
	public LudoFieldType getFieldType() {
		return this.fieldType;
	}
	
	public LudoFieldPos getPos() {
		return this.pos;
	}
	
	public LudoType getType() {
		return this.type;
	}
	
	public LudoState getState() {
		return this.state;
	}
	
	public void setTypeAndState(LudoType type, LudoState state) {
		if (this.type != LudoType.NO) {
			this.lastType = this.type;
		}
		if (this.state != LudoState.SHADOW) {
			this.lastState = this.state;
		}
		this.type = type;
		this.state = state;
		this.updateGraphic(this.type.getImage(state, this.prefSize * 0.95, this.prefSize * 0.95));
	}
	
	public void reset() {
		this.type = this.lastType;
		this.state = this.lastState;
		this.updateGraphic(this.type.getImage(this.state, this.prefSize * 0.95, this.prefSize * 0.95));
	}
	
	public boolean hasFigure() {
		return this.type != LudoType.NO && this.state == LudoState.DEFAULT;
	}
	
	public boolean canSelect() {
		return this.type != LudoType.NO && this.state == LudoState.DEFAULT && this.type == this.client.getPlayer().getType();
	}
	
}
