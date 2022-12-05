package net.vgc.client.fx;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.luis.fxutils.FxUtils;
import net.vgc.Constans;

/**
 *
 * @author Luis-st
 *
 */

public class InputPane extends GridPane {
	
	private final Text inputText;
	private final TextField inputField;
	
	public InputPane(String inputText) {
		this.inputText = new Text(inputText);
		this.inputField = new TextField();
		this.init();
	}
	
	private void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(0.0);
		this.setHgap(0.0);
		this.setGridLinesVisible(Constans.DEBUG);
		this.addColumn(0, FxUtils.makeVBox(Pos.CENTER_LEFT, 0.0, this.inputText), this.inputField);
	}
	
	public Text getInputText() {
		return this.inputText;
	}
	
	public TextField getInputField() {
		return this.inputField;
	}
	
	public String getText() {
		return this.inputField.getText();
	}
	
	public void setText(String text) {
		this.inputField.setText(text);
	}
	
}
