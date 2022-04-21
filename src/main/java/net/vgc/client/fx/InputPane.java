package net.vgc.client.fx;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class InputPane extends GridPane {
	
	protected final Text inputText;
	protected final TextField inputField;
	
	public InputPane(String inputText) {
		this.inputText = new Text(inputText);
		this.inputField = new TextField();
		this.init();
	}
	
	protected void init() {
		this.setAlignment(Pos.CENTER);
		this.setVgap(0.0);
		this.setHgap(0.0);
		this.addColumn(0, FxUtil.makeVerticalBox(Pos.CENTER_LEFT, 0.0, this.inputText), this.inputField);
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
