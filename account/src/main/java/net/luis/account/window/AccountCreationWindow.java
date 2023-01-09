package net.luis.account.window;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.luis.account.AccountServer;
import net.luis.account.account.Account;
import net.luis.account.account.AccountType;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.PropertyListeners;
import net.luis.fxutils.fx.InputPane;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.common.Constants;
import net.luis.common.window.AbstractWindow;
import net.luis.language.TranslationKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
	
	private final AccountServer account;
	private final InputValidationPane<TextField> namePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField(), (field) -> {
		if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
			return InputValidationPane.ValidationState.INVALID;
		} else {
			return InputValidationPane.ValidationState.VALID;
		}
	});
	private final InputPane<TextField> firstNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.first_name"), new TextField());
	private final InputPane<TextField> lastNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.last_name"), new TextField());
	private final InputPane<TextField> mailPane = new InputPane<>(TranslationKey.createAndGet("window.create_account.mail"), new TextField());
	private final InputValidationPane<DatePicker> datePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.birthday"), new DatePicker(), (picker) -> {
		if (StringUtils.trimToEmpty(picker.getEditor().getText()).isEmpty()) {
			return InputValidationPane.ValidationState.DEFAULT;
		} else if (picker.getValue() != null && LocalDate.now().isAfter(picker.getValue())) {
			return InputValidationPane.ValidationState.VALID;
		} else {
			return InputValidationPane.ValidationState.INVALID;
		}
	});	private final InputValidationPane<TextField> passwordPane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.password"), new PasswordField(), (field) -> {
		this.confirmPasswordPane.validateInput();
		String password = StringUtils.trimToEmpty(field.getText());
		if (password.isEmpty()) {
			return InputValidationPane.ValidationState.INVALID;
		} else if (this.isValidPassword(password)) {
			return InputValidationPane.ValidationState.INVALID;
		} else {
			return InputValidationPane.ValidationState.VALID;
		}
	});
	public AccountCreationWindow(AccountServer account, Stage stage) {
		super(stage, 340.0, 365.0);
		this.account = account;
	}
	
	private GridPane main() {
		GridPane pane = FxUtils.makeGrid(Pos.TOP_CENTER, 10.0, 20.0);
		CssUtils.addStyleClass(this.namePane.getInputNode(), "input-validation");
		pane.add(this.namePane, 0, 0);
		CssUtils.addStyleClass(this.passwordPane.getInputNode(), "input-validation");
		pane.add(this.passwordPane, 0, 1);
		CssUtils.addStyleClass(this.confirmPasswordPane.getInputNode(), "input-validation");
		pane.add(this.confirmPasswordPane, 1, 1);
		pane.add(this.firstNamePane, 0, 2);
		pane.add(this.lastNamePane, 1, 2);
		pane.add(this.mailPane, 0, 3);
		this.datePane.getInputNode().setPrefSize(149.0, 25.0);
		this.datePane.addChildNode(CssUtils.addStyleClass(this.datePane.getInputNode().getEditor(), "input-validation"));
		this.datePane.getInputNode().getEditor().textProperty().addListener(PropertyListeners.create(this.datePane::validateInput));
		CssUtils.addStyleClass(this.datePane.getInputNode(), "input-validation");
		pane.add(this.datePane, 0, 4);
		pane.add(FxUtils.makeVBox(Pos.CENTER, 0.0, FxUtils.makeButton(TranslationKey.createAndGet("window.create_account.back"), this::close)), 0, 5);
		pane.add(FxUtils.makeVBox(Pos.CENTER, 0.0, FxUtils.makeButton(TranslationKey.createAndGet("window.create_account.create"), () -> {
			String name = this.namePane.getInputNode().getText();
			String password = this.passwordPane.getInputNode().getText();
			String confirmPassword = this.confirmPasswordPane.getInputNode().getText();
			String firstName = StringUtils.trimToEmpty(this.firstNamePane.getInputNode().getText());
			String lastName = StringUtils.trimToEmpty(this.lastNamePane.getInputNode().getText());
			String mail = StringUtils.trimToEmpty(this.mailPane.getInputNode().getText());
			LocalDate date = this.datePane.getInputNode().getValue();
			if (StringUtils.isBlank(name)) {
				LOGGER.warn("No name set");
				this.namePane.validateInput();
			} else if (StringUtils.isBlank(password)) {
				LOGGER.warn("No password set");
				this.passwordPane.validateInput();
			} else if (StringUtils.isBlank(confirmPassword)) {
				LOGGER.warn("The password has not been confirmed");
				this.confirmPasswordPane.validateInput();
			} else if (this.isValidPassword(password)) {
				LOGGER.warn("The password is not valid");
				this.passwordPane.validateInput();
				this.confirmPasswordPane.validateInput();
			} else if (!password.trim().equals(confirmPassword.trim())) {
				LOGGER.warn("The password was not confirmed correctly");
				this.passwordPane.validateInput();
				this.confirmPasswordPane.validateInput();
			} else if (date == null) {
				LOGGER.warn("No birthday set");
				this.datePane.validateInput();
			} else {
				Account account = this.account.getManager().createAccount(name, mail, password.hashCode(), firstName, lastName, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), AccountType.USER);
				if (account != Account.UNKNOWN) {
					this.close();
				} else {
					this.namePane.getInputNode().setText("");
					this.passwordPane.getInputNode().setText("");
					this.confirmPasswordPane.getInputNode().setText("");
				}
			}
		})), 1, 5);
		return pane;
	}
	
	private boolean isValidPassword(String password) {
		return (!PATTERN.matcher(password).matches() || password.length() <= 4) && !Constants.IDE;
	}
	
	@Override
	protected void onUpdateScene(Scene scene) {
		scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/style.css")).toExternalForm());
	}	private final InputValidationPane<PasswordField> confirmPasswordPane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.confirm_password"), new PasswordField(), (confirmField) -> {
		if (this.passwordPane.getInputNode().getText().isBlank() && confirmField.getText().isBlank()) {
			return InputValidationPane.ValidationState.DEFAULT;
		} else if (this.passwordPane.getInputNode().getText().equals(confirmField.getText())) {
			return InputValidationPane.ValidationState.VALID;
		} else {
			return InputValidationPane.ValidationState.INVALID;
		}
	});
	
	@Override
	public void show() {
		this.updateScene(this.main());
		this.stage.setTitle(TranslationKey.createAndGet("account.window.create"));
		Stage stage = this.account.getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
		this.stage.show();
	}
	
	@Override
	protected void exit() {
	
	}
	

	

	
	
}
