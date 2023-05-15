package net.luis.game.fx;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import net.luis.Constants;
import net.luis.fxutils.EventHandlers;
import net.luis.fxutils.FxUtils;
import net.luis.game.dice.DiceRenderState;

/**
 *
 * @author Luis-st
 *
 */

public class DiceButton extends Button {
	
	private final double prefSize;
	private final Runnable action;
	private int count = 0;
	
	public DiceButton(double prefSize, Runnable action) {
		this.prefSize = prefSize;
		this.action = action;
		this.init();
	}
	
	private void init() {
		this.setPrefSize(this.prefSize, this.prefSize);
		this.updateState();
		if (!Constants.DEBUG_MODE) {
			this.setBackground(null);
		}
		this.setOnAction(EventHandlers.create(this.action));
	}
	
	private void updateState() {
		ImageView image = DiceRenderState.fromCount(this.count).getImage(this.prefSize * 0.9, this.prefSize * 0.9);
		this.setGraphic(FxUtils.makeDefaultVBox(image));
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
		this.updateState();
	}
}
