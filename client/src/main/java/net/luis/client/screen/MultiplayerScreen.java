package net.luis.client.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.client.ClientAccount;
import net.luis.client.fx.ButtonBox;
import net.luis.client.window.LoginWindow;
import net.luis.common.Constants;
import net.luis.common.util.Util;
import net.luis.fxutils.CssUtils;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.fx.InputValidationPane;
import net.luis.language.TranslationKey;
import net.luis.network.ConnectionHandler;
import net.luis.network.packet.Packet;
import net.luis.network.packet.server.ClientJoinPacket;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class MultiplayerScreen extends Screen {
	
	private final Screen backScreen;
	private InputValidationPane<TextField> hostPane;
	private InputValidationPane<TextField> portPane;
	private ButtonBox connectButtonBox;
	private ButtonBox connectLocalButtonBox;
	private ButtonBox backButtonBox;
	
	public MultiplayerScreen(Screen backScreen) {
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
		if (this.client.isLoggedIn()) {
			return true;
		} else {
			if (this.client.getLoginWindow() == null) {
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
				ClientAccount account = this.client.getAccount();
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
			ClientAccount account = this.client.getAccount();
			this.connectAndSend("127.0.0.1", 8080, new ClientJoinPacket(account.name(), account.uuid()));
		}
	}
	
	private void connectAndSend(String host, int port, Packet packet) {
		ConnectionHandler handler = this.client.getServerHandler();
		try {
			handler.connect(host, port);
			Util.runDelayed("DelayedPacketSender", 250, () -> {
				if (handler.isConnected()) {
					handler.send(packet);
				} else {
					LOGGER.warn("Unable to send packet of type {} to virtual game collection server, since connection is closed", packet.getClass().getSimpleName());
				}
			});
		} catch (Exception e) {
			LOGGER.warn("Fail to connect to virtual game collection server", e);
		}
	}
	
	private void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	@Override
	protected Pane createPane() {
		GridPane outerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtils.makeGrid(Pos.CENTER, 10.0, 20.0);
		if (Constants.IDE) {
			innerPane.addColumn(0, this.connectButtonBox, this.connectLocalButtonBox, this.backButtonBox);
		} else {
			innerPane.addColumn(0, this.connectButtonBox, this.backButtonBox);
		}
		outerPane.addColumn(0, this.hostPane, this.portPane, innerPane);
		return outerPane;
	}
	
	@Override
	protected void onSceneShow(Scene scene) {
		scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/style.css")).toExternalForm());
	}
}
