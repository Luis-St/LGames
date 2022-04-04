package net.vgc.client.fx;

import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import net.vgc.util.Util;

public class FxAnimationUtil {
	
	public static void makeEmptyText(TextField field, int millis) {
		field.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5.0), BorderWidths.DEFAULT)));
		Util.runDelayed("TextFieldEmpty", millis, () -> {
			field.setBorder(Border.EMPTY);
		});
	}
	
}
