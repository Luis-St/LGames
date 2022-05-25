package net.vgc.client.fx.game;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import net.vgc.Constans;
import net.vgc.client.fx.Box;
import net.vgc.oldgame.ttt.TTTState;
import net.vgc.oldgame.ttt.TTTType;

public class TTTButton extends ToggleButton {
	
	protected final ToggleGroup group;
	protected final double prefSize;
	protected final int vMap;
	protected final int hMap;
	protected TTTType type;
	protected TTTState state;
	
	public TTTButton(ToggleGroup group, double prefSize, int vMap, int hMap) {
		this.group = group;
		this.prefSize = prefSize;
		this.vMap = vMap;
		this.hMap = hMap;
		this.init();
	}
	
	protected void init() {
		this.type = TTTType.NO;
		this.state = TTTState.DEFAULT;
		this.setToggleGroup(this.group);
		this.setPrefSize(this.prefSize, this.prefSize);
		if (!Constans.DEBUG) {
			this.setBackground(null);
		}
	}
	
	public int getVMap() {
		return this.vMap;
	}
	
	public int getHMap() {
		return this.hMap;
	}
	
	public TTTType getType() {
		return this.type;
	}
	
	public TTTState getState() {
		return this.state;
	}
	
	public void setTypeAndState(TTTType type, TTTState state) {
		this.type = type;
		this.state = state;
		if (this.type != TTTType.NO) {
			ImageView image = this.type.getImage(state, this.prefSize * 0.95, this.prefSize * 0.95);
			if (image != null) {
				this.setGraphic(new Box<>(image, Pos.CENTER));
			} else {
				this.setGraphic(null);
			}
		} else {
			this.setGraphic(null);
		}
	}
	
}
