package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.Constants;
import net.luis.client.account.ClientAccount;
import net.luis.client.window.LoginWindow;
import net.luis.fx.ScreenScene;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.language.TranslationKey;
import net.luis.network.Connection;
import net.luis.network.packet.Packet;
import net.luis.network.packet.server.ClientJoinPacket;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class MultiplayerScreen extends ClientScreen {
	
	private static final Logger LOGGER = LogManager.getLogger(MultiplayerScreen.class);
	
	private final ClientScreen backScreen;
	private InputValidationPane<TextField> hostPane;
	private InputValidationPane<TextField> portPane;
	private Button connectButton;
	private Button connectLocalButton;
	private Button backButton;
	
	public MultiplayerScreen(ClientScreen backScreen) {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
		this.backScreen = Objects.requireNonNull(backScreen, "Back screen must not be null");
	}
	
	@Override
	public void init() {
		this.hostPane = new InputValidationPane<>(TranslationKey.createAndGet("screen.multiplayer.server_host"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		CssUtils.addStyleClass(this.hostPane.getInputNode(), "input-validation");
		this.portPane = new InputValidationPane<>(TranslationKey.createAndGet("screen.multiplayer.server_port"), new TextField(), (field) -> {
			if (StringUtils.trimToEmpty(field.getText()).isEmpty()) {
				return InputValidationPane.ValidationState.INVALID;
			} else {
				return InputValidationPane.ValidationState.VALID;
			}
		});
		CssUtils.addStyleClass(this.portPane.getInputNode(), "input-validation");
		this.connectButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.multiplayer.connect"), this::handleConnect);
		this.connectLocalButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.multiplayer.connect_local"), this::handleConnectLocal);
		this.backButton = FxUtils.makeButton(TranslationKey.createAndGet("window.login.back"), this::handleBack);
	}
	
	private boolean canConnect() {
		if (this.client.get().getAccountManager().isLoggedIn()) {
			return true;
		} else {
			if (this.client.get().getAccountManager().getLoginWindow() == null) {
				LoginWindow window = new LoginWindow(this.client.get(), new Stage());
				window.show();
			}
			return false;
		}
	}
	
	private void handleConnect() {
		if (this.canConnect()) {
			String host = StringUtils.trimToEmpty(this.hostPane.getInputNode().getText());
			String port = StringUtils.trimToEmpty(this.portPane.getInputNode().getText());
			if (host.isEmpty() && port.isEmpty()) {
				this.hostPane.getInputNode().setText("127.0.0.1");
				this.hostPane.validateInput();
			} else if (host.isEmpty()) {
				this.hostPane.getInputNode().setText("127.0.0.1");
				this.hostPane.validateInput();
			} else if (port.isEmpty()) {
				this.portPane.validateInput();
			} else {
				ClientAccount account = Objects.requireNonNull(this.client.get().getAccountManager().getAccount());
				this.connectAndSend(host, Integer.parseInt(port), new ClientJoinPacket(account.name(), account.uniqueId()));
			}
		}
	}
	
	private void handleConnectLocal() {
		if (this.canConnect()) {
			this.hostPane.getInputNode().setText("127.0.0.1");
			this.hostPane.validateInput();
			this.portPane.getInputNode().setText("8080");
			this.portPane.validateInput();
			ClientAccount account = Objects.requireNonNull(this.client.get().getAccountManager().getAccount());
			this.connectAndSend("127.0.0.1", 8080, new ClientJoinPacket(account.name(), account.uniqueId()));
		}
	}
	
	private void connectAndSend(@NotNull String host, int port, @NotNull Packet packet) {
		try {
			Connection.send(this.client.get().getNetworkInstance().open(host, port), packet);
		} catch (Exception e) {
			LOGGER.warn("Fail to connect to virtual game collection server", e);
		}
	}
	
	private void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	@Override
	protected @NotNull Pane createPane() {
		GridPane outerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		if (Constants.DEV_MODE) {
			innerPane.addColumn(0, FxUtils.makeDefaultVBox(this.connectButton), FxUtils.makeDefaultVBox(this.connectLocalButton), FxUtils.makeDefaultVBox(this.backButton));
		} else {
			innerPane.addColumn(0, FxUtils.makeDefaultVBox(this.connectButton), FxUtils.makeDefaultVBox(this.backButton));
		}
		outerPane.addColumn(0, this.hostPane, this.portPane, innerPane);
		return outerPane;
	}
	
	@Override
	protected @NotNull ScreenScene createScene() {
		ScreenScene scene = super.createScene();
		scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/style.css")).toExternalForm());
		return scene;
	}
}
