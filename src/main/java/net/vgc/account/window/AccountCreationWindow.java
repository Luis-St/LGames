package net.vgc.account.window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.vgc.account.AccountServer;
import net.vgc.client.fx.FxAnimationUtil;
import net.vgc.client.fx.FxUtil;
import net.vgc.language.TranslationKey;

public class AccountCreationWindow {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final AccountServer accountServer;
	protected final Stage stage;
	
	public AccountCreationWindow(AccountServer accountServer, Stage stage) {
		this.accountServer = accountServer;
		this.stage = stage;
	}
	
	protected Pane main() {
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		TextField nameField = new TextField();
		VBox nameBox = FxUtil.makeCentered(nameField);
		TextField passwordField = new TextField();
		VBox passwordBox = FxUtil.makeCentered(passwordField);
		GridPane guestPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 0.0);
		guestPane.setAlignment(Pos.CENTER);
		ToggleGroup toggleGroup = new ToggleGroup();
		ToggleButton falseButton = new ToggleButton(TranslationKey.createAndGet("window.create_account.false"));
		falseButton.setToggleGroup(toggleGroup);
		ToggleButton trueButton = new ToggleButton(TranslationKey.createAndGet("window.create_account.true"));
		trueButton.setToggleGroup(toggleGroup);
		guestPane.addRow(0, falseButton, trueButton);
		Button closeButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.close"), () -> {
			this.stage.close();
		});
		Button createButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.create"), () -> {
			String name = nameField.getText();
			String password = passwordField.getText();
			boolean gueatFalse = falseButton.isSelected();
			boolean guestTrue = trueButton.isSelected();
			if (name.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(nameField, 750);
				LOGGER.info("Name is not set");
			} else if (password.trim().isEmpty()) {
				FxAnimationUtil.makeEmptyText(passwordField, 750);
				LOGGER.info("Password is not set");
			} else if (!gueatFalse && !guestTrue) {
				FxAnimationUtil.makeNotToggled(falseButton, 750);
				FxAnimationUtil.makeNotToggled(trueButton, 750);
				LOGGER.info("No account type selected");
			} else {
				this.accountServer.getAgent().createAccount(name, password, !gueatFalse && guestTrue);
				nameField.setText("");
				passwordField.setText("");
				toggleGroup.selectToggle(null);
			}
		});
		pane.addRow(0, new Text(TranslationKey.createAndGet("window.login.name")), nameBox);
		pane.addRow(1, new Text(TranslationKey.createAndGet("window.login.password")), passwordBox);
		pane.addRow(3, new Text(TranslationKey.createAndGet("window.login.guest")), guestPane);
		pane.addRow(4, closeButton, createButton);
		return pane;
	}
	
	public void show() {
		this.stage.setTitle(TranslationKey.createAndGet("account.window.create"));
		this.stage.setScene(new Scene(this.main(), 275.0, 200.0));
		Stage stage = this.accountServer.getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
		this.stage.show();
	}
	
}
