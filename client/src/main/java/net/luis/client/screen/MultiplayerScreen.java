package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.Constants;
import net.luis.client.account.ClientAccount;
import net.luis.client.window.LoginWindow;
import net.luis.fx.ButtonBox;
import net.luis.fx.ScreenScene;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.game.network.NetworkController;
import net.luis.language.TranslationKey;
import net.luis.network.packet.Packet;
import net.luis.network.packet.server.ClientJoinPacket;
import net.luis.utility.Util;
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
	private ButtonBox connectButtonBox;
	private ButtonBox connectLocalButtonBox;
	private ButtonBox backButtonBox;
	
	public MultiplayerScreen(@NotNull ClientScreen backScreen) {
		super(TranslationKey.createAndGet("client.constans.name"), 600, 600);
		this.backScreen = backScreen;
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
		this.connectButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.multiplayer.connect"), this::handleConnect);
		this.connectLocalButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.multiplayer.connect_local"), this::handleConnectLocal);
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
	}
	
	private boolean canConnect() {
		if (this.client.getAccountManager().isLoggedIn()) {
			return true;
		} else {
			if (this.client.getAccountManager().getLoginWindow() == null) {
				LoginWindow window = new LoginWindow(this.client, new Stage());
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
				ClientAccount account = Objects.requireNonNull(this.client.getAccountManager().getAccount());
				this.connectAndSend(host, Integer.parseInt(port), new ClientJoinPacket(account.name(), account.uuid()));
			}
		}
	}
	
	private void handleConnectLocal() {
		if (this.canConnect()) {
			this.hostPane.getInputNode().setText("127.0.0.1");
			this.hostPane.validateInput();
			this.portPane.getInputNode().setText("8080");
			this.portPane.validateInput();
			ClientAccount account = Objects.requireNonNull(this.client.getAccountManager().getAccount());
			this.connectAndSend("127.0.0.1", 8080, new ClientJoinPacket(account.name(), account.uuid()));
		}
	}
	
	private void connectAndSend(@NotNull String host, int port, @NotNull Packet packet) {
		NetworkController networkController = this.client.getServerController();
		try {
			networkController.getInstance().open(host, port);
			Util.runDelayed("DelayedPacketSender", 250, () -> {
				networkController.send(packet);
			});
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
			innerPane.addColumn(0, this.connectButtonBox, this.connectLocalButtonBox, this.backButtonBox);
		} else {
			innerPane.addColumn(0, this.connectButtonBox, this.backButtonBox);
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
