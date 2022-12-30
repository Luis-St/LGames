package net.vgc.client.fx;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class FxAnimationUtil {
	
	public static void makeEmptyText(TextField field, int millis) {
		field.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5.0), BorderWidths.DEFAULT)));
		Util.runDelayed("TextFieldEmpty", millis, () -> {
			field.setBorder(Border.EMPTY);
		});
	}
	
	public static void makeNotToggled(ToggleButton button, int millis) {
		button.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5.0), BorderWidths.DEFAULT)));
		Util.runDelayed("ButtonNotToggled", millis, () -> {
			button.setBorder(Border.EMPTY);
		});
	}
	
}
