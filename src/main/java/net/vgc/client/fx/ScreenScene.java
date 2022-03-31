package net.vgc.client.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.vgc.client.fx.input.InputContext;
import net.vgc.client.fx.input.MouseInputContext;
import net.vgc.client.fx.input.PositionInputContext;
import net.vgc.client.fx.input.ScrollInputContext;
import net.vgc.client.screen.Screen;
import net.vgc.util.Tickable;

public class ScreenScene extends Scene implements Tickable {

	protected final Screen screen;

	public ScreenScene(Parent root, double width, double height, Screen screen) {
		super(root, width, height);
		this.screen = screen;
	}
	
	public Screen getScreen() {
		return this.screen;
	}
	
	public boolean shouldCenter() {
		return this.screen.shouldCenter;
	}

	public void setInputListeners() {
		this.setOnKeyPressed((event) -> {
			this.screen.keyPressed(event.getCode(), new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));

		});
		this.setOnKeyReleased((event) -> {
			this.screen.keyReleased(event.getCode(), new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));
		});
		this.setOnKeyTyped((event) -> {
			this.screen.keyTyped(event.getCode(), event.getCharacter(), new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));
		});
		this.setOnMouseClicked((event) -> {
			InputContext inputContext = new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
			PositionInputContext positionContext = new PositionInputContext(event.getX(), event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY());
			this.screen.mouseClicked(new MouseInputContext(inputContext, positionContext, event.getButton(), event.isPrimaryButtonDown(), event.isMiddleButtonDown(), event.isSecondaryButtonDown()));
		});
		this.setOnMouseEntered((event) -> {
			InputContext inputContext = new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
			PositionInputContext positionContext = new PositionInputContext(event.getX(), event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY());
			this.screen.mouseEntered(new MouseInputContext(inputContext, positionContext, event.getButton(), event.isPrimaryButtonDown(), event.isMiddleButtonDown(), event.isSecondaryButtonDown()));
		});
		this.setOnMouseExited((event) -> {
			InputContext inputContext = new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
			PositionInputContext positionContext = new PositionInputContext(event.getX(), event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY());
			this.screen.mouseExited(new MouseInputContext(inputContext, positionContext, event.getButton(), event.isPrimaryButtonDown(), event.isMiddleButtonDown(), event.isSecondaryButtonDown()));
		});
		this.setOnScrollStarted((event) -> {
			InputContext inputContext = new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
			PositionInputContext positionContext = new PositionInputContext(event.getX(), event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY());
			this.screen.scrollStarted(new ScrollInputContext(inputContext, positionContext, event.getDeltaX(), event.getDeltaY()));
		});
		this.setOnScrollFinished((event) -> {
			InputContext inputContext = new InputContext(event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
			PositionInputContext positionContext = new PositionInputContext(event.getX(), event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY());
			this.screen.scrollFinished(new ScrollInputContext(inputContext, positionContext, event.getDeltaX(), event.getDeltaY()));
		});
	}

	@Override
	public void tick() {
		this.screen.tick();
	}

}
