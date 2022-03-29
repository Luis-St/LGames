package net.vgc.client.fx.input;

import javafx.scene.input.MouseButton;

public record MouseInputContext(InputContext inputContext, PositionInputContext positionContext, MouseButton button, boolean primary, boolean middle, boolean secondary) {

}
