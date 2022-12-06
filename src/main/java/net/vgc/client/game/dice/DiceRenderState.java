package net.vgc.client.game.dice;

import org.jetbrains.annotations.Nullable;

import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.vgc.network.Network;
import net.vgc.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum DiceRenderState implements EnumRepresentable {
	
	ZERO("zero", 0, "textures/dice/dice"), ONE("one", 1, "textures/dice/dice_1"), TWO("two", 2, "textures/dice/dice_2"), THREE("three", 3, "textures/dice/dice_3"), FOUR("four", 4, "textures/dice/dice_4"), FIVE("five", 5, "textures/dice/dice_5"),
	SIX("six", 6, "textures/dice/dice_6");
	
	private final String name;
	private final int id;
	private final String path;
	
	private DiceRenderState(String name, int id, String path) {
		this.name = name;
		this.id = id;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Nullable
	public ImageView getImage(double width, double height) {
		return FxUtils.makeImageView(Network.INSTANCE.getResourceDirectory().resolve(this.path + ".png").toString(), width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Nullable
	public static DiceRenderState fromCount(int count) {
		DiceRenderState state = EnumRepresentable.fromId(DiceRenderState.class, count);
		return state == null ? ZERO : state;
	}
	
}
