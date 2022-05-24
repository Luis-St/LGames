package net.vgc.client.fx.game;

import javax.annotation.Nullable;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.vgc.Main;
import net.vgc.client.Client;
import net.vgc.client.fx.Box;
import net.vgc.client.fx.FxUtil;
import net.vgc.game.ludo.LudoState;
import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;

public class LudoButton extends ToggleButton {
	
	protected final Client client;
	protected final ToggleGroup group;
	protected final ImageView fieldImage;
	protected final LudoFieldType fieldType;
	protected final LudoFieldPos pos;
	protected final double prefSize;
	protected LudoType type;
	protected LudoState state;
	
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
		this.type = type;
		this.state = state;
		this.updateGraphic(this.type.getImage(state, this.prefSize * 0.95, this.prefSize * 0.95));
	}
	
	public void reset() {
		this.type = LudoType.NO;
		this.state = LudoState.DEFAULT;
		this.updateGraphic(null);
	}
	
	public boolean hasFigure() {
		return this.type != LudoType.NO && this.state == LudoState.DEFAULT;
	}
	
	public boolean canSelect() {
		Main.LOGGER.debug("#canSelect type {}, {} != {}", this.type != LudoType.NO, this.type, LudoType.NO);
		Main.LOGGER.debug("#canSelect state {}, {} != {}", this.state == LudoState.DEFAULT, this.state, LudoState.DEFAULT);
		Main.LOGGER.debug("#canSelect player {}, {} != {}", this.type == this.client.getPlayer().getType(), this.type, this.client.getPlayer().getType());
		Main.LOGGER.debug("#canSelect all {}", (this.type != LudoType.NO && this.state == LudoState.DEFAULT && this.type == this.client.getPlayer().getType()));
		return this.type != LudoType.NO && this.state == LudoState.DEFAULT && this.type == this.client.getPlayer().getType();
	}
	
}
