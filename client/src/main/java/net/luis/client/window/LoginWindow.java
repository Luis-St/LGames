package net.luis.client.window;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.Constants;
import net.luis.account.account.LoginType;
import net.luis.client.Client;
import net.luis.client.account.ClientAccount;
import net.luis.fx.Box;
import net.luis.fx.window.AbstractWindow;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.EventHandlers;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.PropertyListeners;
import net.luis.fxutils.fx.InputPane;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.language.TranslationKey;
import net.luis.network.SecondaryConnection;
import net.luis.network.packet.account.ClientLoginPacket;
import net.luis.network.packet.account.ClientLogoutPacket;
import net.luis.network.packet.account.ClientRegistrationPacket;
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

public class LoginWindow extends AbstractWindow {
	
	private static final Logger LOGGER = LogManager.getLogger(LoginWindow.class);
	private static final Pattern PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
	
	private final Client client;
	private final SecondaryConnection connection;
	
	public LoginWindow(@NotNull Client client, @NotNull Stage stage) {
		super(stage, 350, 225.0);
		this.client = client;
		this.client.getAccountManager().setLoginWindow(this);
		this.connection = new SecondaryConnection();
		this.connection.open(this.client.getAccountHost(), this.client.getAccountPort());
	}
	
	private @NotNull Pane main() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		Button registrationButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.register"), () -> this.updateScene(this.registration1()));
		Button loginButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.login"), () -> this.updateScene(this.loginSelect()));
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), this::close);
		pane.addColumn(0, new Box<>(registrationButton), new Box<>(loginButton), new Box<>(closeButton));
		return pane;
	}
	
	private @NotNull Pane profile() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		ClientAccount account = Objects.requireNonNull(this.client.getAccountManager().getAccount());
		InputPane<TextField> namePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField());
		namePane.getInputNode().setEditable(false);
		namePane.getInputNode().setText(account.name());
		pane.add(namePane, 0, 0);
		Button logoutButton = FxUtils.makeButton(TranslationKey.createAndGet("window.logout.logout"), () -> {
			this.connection.send(new ClientLogoutPacket(account.name(), account.id(), account.uuid()));
		});
		logoutButton.setAlignment(Pos.CENTER);
		if (!account.guest() && this.client.getAccountManager().isPasswordCachedLocal()) {
			InputPane<TextField> passwordPane = new InputPane<>(TranslationKey.createAndGet("window.create_account.password"), new TextField());
			passwordPane.getInputNode().setEditable(false);
			passwordPane.getInputNode().setText(this.client.getAccountManager().getPassword());
			pane.add(passwordPane, 0, 1);
			pane.add(logoutButton, 0, 2);
		} else {
			pane.add(logoutButton, 0, 1);
		}
		return pane;
	}
	
	private @NotNull Pane registration1() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		InputValidationPane<TextField> namePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
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
		CssUtils.addStyleClass(namePane.getInputNode(), "input-validation");
		CssUtils.addStyleClass(passwordPane.getValue().getInputNode(), "input-validation");
		CssUtils.addStyleClass(confirmPasswordPane.getInputNode(), "input-validation");
		pane.add(namePane, 0, 0);
		pane.add(passwordPane.getValue(), 0, 1);
		pane.add(confirmPasswordPane, 1, 1);
		pane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), () -> this.updateScene(this.client.getAccountManager().isLoggedIn() ? this.profile() : this.main())), 0, 2);
		pane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.next"), () -> {
			String name = namePane.getInputNode().getText();
			String password = passwordPane.getValue().getInputNode().getText();
			String confirmPassword = confirmPasswordPane.getInputNode().getText();
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
			} else {
				this.updateScene(this.registration2(name, password));
			}
		}), 1, 2);
		return pane;
	}
	
	private boolean isValidPassword(String password) {
		return (!PATTERN.matcher(password).matches() || password.length() <= 4) && !Constants.DEV_MODE;
	}
	
	private @NotNull Pane registration2(@NotNull String name, @NotNull String password) {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		InputPane<TextField> firstNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.first_name"), new TextField());
		InputPane<TextField> lastNamePane = new InputPane<>(TranslationKey.createAndGet("window.create_account.last_name"), new TextField());
		InputPane<TextField> mailPane = new InputPane<>(TranslationKey.createAndGet("window.create_account.mail"), new TextField());
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
		pane.add(firstNamePane, 0, 0);
		pane.add(lastNamePane, 1, 0);
		pane.add(mailPane, 0, 1);
		pane.add(datePane, 1, 1);
		Button backButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), () -> this.updateScene(this.registration1()));
		Button registrationButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.register"), () -> {
			String firstName = StringUtils.trimToEmpty(firstNamePane.getInputNode().getText());
			String lastName = StringUtils.trimToEmpty(lastNamePane.getInputNode().getText());
			String mail = StringUtils.trimToEmpty(mailPane.getInputNode().getText());
			LocalDate date = datePane.getInputNode().getValue();
			if (date == null) {
				LOGGER.warn("No birthday set");
				datePane.validateInput();
			} else {
				this.client.getAccountManager().setPassword(password);
				this.connection.send(new ClientRegistrationPacket(name, mail, password.hashCode(), firstName, lastName, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())));
			}
		});
		pane.add(backButton, 0, 2);
		pane.add(registrationButton, 1, 2);
		return pane;
	}
	
	private @NotNull Pane loginSelect() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		Button userButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.user"), () -> this.updateScene(this.loginUser()));
		Button guestButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.guest"), () -> this.updateScene(this.loginGuest()));
		Button backButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), () -> this.updateScene(this.client.getAccountManager().isLoggedIn() ? this.profile() : this.main()));
		pane.addColumn(0, new Box<>(userButton), new Box<>(guestButton), new Box<>(backButton));
		return pane;
	}
	
	private @NotNull Pane loginUser() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		InputValidationPane<TextField> namePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		InputValidationPane<TextField> passwordPane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.password"), new PasswordField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		CssUtils.addStyleClass(namePane.getInputNode(), "input-validation");
		CssUtils.addStyleClass(passwordPane.getInputNode(), "input-validation");
		Runnable loginRunnable = () -> {
			String name = StringUtils.trimToEmpty(namePane.getInputNode().getText());
			String password = StringUtils.trimToEmpty(passwordPane.getInputNode().getText());
			if (name.isEmpty()) {
				namePane.validateInput();
				LOGGER.info("Username is not set");
			} else if (password.isEmpty()) {
				passwordPane.validateInput();
				LOGGER.info("Password is not set");
			} else {
				this.client.getAccountManager().setPassword(password);
				this.connection.send(new ClientLoginPacket(LoginType.USER_LOGIN, name, password.hashCode()));
			}
		};
		passwordPane.getInputNode().setOnAction(EventHandlers.create(loginRunnable));
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), () -> this.updateScene(this.loginSelect())), 0, 0);
		innerPane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.login"), loginRunnable), 1, 0);
		pane.addColumn(1, namePane, passwordPane, innerPane);
		return pane;
	}
	
	private @NotNull Pane loginGuest() {
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		InputValidationPane<TextField> namePane = new InputValidationPane<>(TranslationKey.createAndGet("window.create_account.name"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		CssUtils.addStyleClass(namePane.getInputNode(), "input-validation");
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), () -> this.updateScene(this.loginSelect())), 0, 0);
		innerPane.add(FxUtils.makeButton(TranslationKey.createAndGet("window.login.login"), () -> {
			String name = StringUtils.trimToEmpty(namePane.getInputNode().getText());
			if (name.isEmpty()) {
				namePane.validateInput();
				LOGGER.warn("Guest name is not set");
			} else {
				this.client.getAccountManager().setPassword("");
				this.connection.send(new ClientLoginPacket(LoginType.GUEST_LOGIN, name, "".hashCode()));
			}
		}), 1, 0);
		pane.addColumn(1, namePane, innerPane);
		return pane;
	}
	
	public void handleLoggedIn() {
		this.stage.setTitle(TranslationKey.createAndGet("screen.menu.profile"));
		this.updateScene(this.profile());
	}
	
	public void handleLoggedOut() {
		this.stage.setTitle(TranslationKey.createAndGet("screen.menu.login"));
		this.updateScene(this.main());
	}
	
	@Override
	protected void onUpdateScene(@NotNull Scene scene) {
		scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/style.css")).toExternalForm());
	}
	
	@Override
	public void show() {
		boolean loggedIn = this.client.getAccountManager().isLoggedIn();
		this.updateScene(loggedIn ? this.profile() : this.main());
		this.stage.setTitle(loggedIn ? TranslationKey.createAndGet("screen.menu.profile") : TranslationKey.createAndGet("screen.menu.login"));
		Stage stage = this.client.getStage();
		this.stage.setX(stage.getX() + stage.getWidth());
		this.stage.setY(stage.getY());
		this.stage.show();
	}
	
	@Override
	protected void exit() {
		this.client.getAccountManager().setLoginWindow(null);
		if (!this.client.getAccountManager().isLoggedIn()) {
			this.client.getAccountController().getInstance().close();
		}
	}
	
}
