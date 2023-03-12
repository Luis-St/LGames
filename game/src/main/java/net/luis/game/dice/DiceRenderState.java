package net.luis.game.dice;

import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.application.FxApplication;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public enum DiceRenderState {
	
	ZERO("zero", 0, "textures/dice/dice"), ONE("one", 1, "textures/dice/dice_1"), TWO("two", 2, "textures/dice/dice_2"), THREE("three", 3, "textures/dice/dice_3"), FOUR("four", 4, "textures/dice/dice_4"), FIVE("five", 5, "textures/dice/dice_5"),
	SIX("six", 6, "textures/dice/dice_6");
	
	private final String name;
	private final int count;
	private final String path;
	
	DiceRenderState(@NotNull String name, int count, @NotNull String path) {
		this.name = name;
		this.count = count;
		this.path = path;
	}
	
	public static @NotNull DiceRenderState fromCount(int count) {
		for (DiceRenderState state : values()) {
			if (state.getCount() == count) {
				return state;
			}
		}
		return ZERO;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public @NotNull ImageView getImage(double width, double height) {
		return FxUtils.makeImageView(FxApplication.getInstance().getResourceManager().resourceDirectory().resolve(this.path + ".png").toString(), width, height);
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
	
}
