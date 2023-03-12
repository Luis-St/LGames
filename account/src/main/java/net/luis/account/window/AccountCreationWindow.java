package net.luis.account.window;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.luis.Constants;
import net.luis.account.AccountServer;
import net.luis.account.account.Account;
import net.luis.account.account.AccountType;
import net.luis.fx.window.AbstractWindow;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.PropertyListeners;
import net.luis.fxutils.fx.InputPane;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.language.TranslationKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-st
 *
 */

public class AccountCreationWindow extends AbstractWindow {
	
	private static final Logger LOGGER = LogManager.getLogger(AccountCreationWindow.class);
	private static final Pattern PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
	
	public AccountCreationWindow(@NotNull Stage stage) {
		super(stage, 340.0, 365.0);
	}
	
	private @NotNull GridPane main() {
		GridPane pane = FxUtils.makeGrid(Pos.TOP_CENTER, 10.0, 20.0);
		InputValidationPane<TextField> namePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		CssUtils.addStyleClass(namePane.getInputNode(), "input-validation");
		pane.add(namePane, 0, 0);
		MutableObject<InputValidationPane<TextField>> passwordPane = new MutableObject<>();
		InputValidationPane<PasswordField> confirmPasswordPane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.confirm_password"), new PasswordField(), (confirmField) -> {
			if (passwordPane.getValue().getInputNode().getText().isBlank() && confirmField.getText().isBlank()) {
				return InputValidationPane.ValidationState.DEFAULT;
			} else if (passwordPane.getValue().getInputNode().getText().equals(confirmField.getText())) {
				return InputValidationPane.ValidationState.VALID;
			} else {
				return InputValidationPane.ValidationState.INVALID;
			}
		});
		passwordPane.setValue(new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.password"), new PasswordField(), (field) -> {
			confirmPasswordPane.validateInput();
			String password = StringUtils.trimToEmpty(field.getText());
			if (password.isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else if (this.isValidPassword(password)) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		}));
		CssUtils.addStyleClass(passwordPane.getValue().getInputNode(), "input-validation");
		pane.add(passwordPane.getValue(), 0, 1);
		CssUtils.addStyleClass(confirmPasswordPane.getInputNode(), "input-validation");
		pane.add(confirmPasswordPane, 1, 1);
		InputPane<TextField> firstNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.first_name"), new TextField());
		pane.add(firstNamePane, 0, 2);
		InputPane<TextField> lastNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.last_name"), new TextField());
		pane.add(lastNamePane, 1, 2);
		InputPane<TextField> mailPane = new InputPane<>(TranslationKey.createAndGet("window.create_account.mail"), new TextField());
		pane.add(mailPane, 0, 3);
		InputValidationPane<DatePicker> datePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.birthday"), new DatePicker(), (picker) -> {
			if (StringUtils.trimToEmpty(picker.getEditor().getText()).isEmpty()) {
				return InputValidationPane.ValidationState.DEFAULT;
			} else if (picker.getValue() != null && LocalDate.now().isAfter(picker.getValue())) {
				return InputValidationPane.ValidationState.VALID;
			} else {
				return InputValidationPane.ValidationState.INVALID;
			}
		});
		datePane.getInputNode().setPrefSize(149.0, 25.0);
		datePane.addChildNode(CssUtils.addStyleClass(datePane.getInputNode().getEditor(), "input-validation"));
		datePane.getInputNode().getEditor().textProperty().addListener(PropertyListeners.create(datePane::validateInput));
		CssUtils.addStyleClass(datePane.getInputNode(), "input-validation");
		pane.add(datePane, 0, 4);
		pane.add(FxUtils.makeVBox(Pos.CENTER, 0.0, FxUtils.makeButton(TranslationKey.createAndGet("window.create_account.back"), this::close)), 0, 5);
		pane.add(FxUtils.makeVBox(Pos.CENTER, 0.0, FxUtils.makeButton(TranslationKey.createAndGet("window.create_account.create"), () -> {
			String name = namePane.getInputNode().getText();
			String password = passwordPane.getValue().getInputNode().getText();
			String confirmPassword = confirmPasswordPane.getInputNode().getText();
			String firstName = StringUtils.trimToEmpty(firstNamePane.getInputNode().getText());
			String lastName = StringUtils.trimToEmpty(lastNamePane.getInputNode().getText());
			String mail = StringUtils.trimToEmpty(mailPane.getInputNode().getText());
			LocalDate date = datePane.getInputNode().getValue();
			if (StringUtils.isBlank(name)) {
				LOGGER.warn("No name set");
				namePane.validateInput();
			} else if (StringUtils.isBlank(password)) {
				LOGGER.warn("No password set");
				passwordPane.getValue().validateInput();
			} else if (StringUtils.isBlank(confirmPassword)) {
				LOGGER.warn("The password has not been confirmed");
				confirmPasswordPane.validateInput();
			} else if (this.isValidPassword(password)) {
				LOGGER.warn("The password is not valid");
				passwordPane.getValue().validateInput();
				confirmPasswordPane.validateInput();
			} else if (!password.trim().equals(confirmPassword.trim())) {
				LOGGER.warn("The password was not confirmed correctly");
				passwordPane.getValue().validateInput();
				confirmPasswordPane.validateInput();
			} else if (date == null) {
				LOGGER.warn("No birthday set");
				datePane.validateInput();
			} else {
				Account account = AccountServer.getInstance().getAccountAgent().createAccount(name, mail, password.hashCode(), firstName, lastName, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), AccountType.USER);
				if (account != Account.UNKNOWN) {
					this.close();
				} else {
					namePane.getInputNode().setText("");
					passwordPane.getValue().getInputNode().setText("");
					confirmPasswordPane.getInputNode().setText("");
				}
			}
		})), 1, 5);
		return pane;
	}
	
	private boolean isValidPassword(@NotNull String password) {
		return (!PATTERN.matcher(password).matches() || password.length() <= 4) && !Constants.DEV_MODE;
	}
	
	@Override
	protected void onUpdateScene(@NotNull Scene scene) {
		scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/style.css")).toExternalForm());
	}
	
	@Override
	public void show() {
		this.updateScene(this.main());
		this.stage.setTitle(TranslationKey.createAndGet("account.window.create"));
		Stage stage = AccountServer.getInstance().getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
		this.stage.show();
	}
	
	@Override
	protected void exit() {
	
	}
	
	
}
