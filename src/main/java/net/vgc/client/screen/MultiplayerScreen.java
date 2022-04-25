package net.vgc.client.screen;

import org.apache.commons.lang3.StringUtils;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.vgc.account.PlayerAccount;
import net.vgc.client.fx.ButtonBox;
import net.vgc.client.fx.FxAnimationUtil;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.fx.InputPane;
import net.vgc.client.window.LoginWindow;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.ConnectionHandler;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.server.ClientJoinPacket;
import net.vgc.util.Util;

public class MultiplayerScreen extends Screen {
	
	protected final Screen backScreen;
	protected InputPane hostInputPane;
	protected InputPane portInputPane;
	protected ButtonBox connectButtonBox;
	protected ButtonBox backButtonBox;
	
	public MultiplayerScreen(Screen backScreen) {
		this.backScreen = backScreen;
	}
	
	@Override
	public void init() {
		this.hostInputPane = new InputPane(TranslationKey.createAndGet("screen.multiplayer.server_host"));
		this.portInputPane = new InputPane(TranslationKey.createAndGet("screen.multiplayer.server_port"));
		this.connectButtonBox = new ButtonBox(TranslationKey.createAndGet("screen.multiplayer.connect"), this::handleConnect);
		this.backButtonBox = new ButtonBox(TranslationKey.createAndGet("window.login.back"), this::handleBack);
	}
	
	protected void handleConnect() {
		if (this.client.isLoggedIn()) {
			String host = StringUtils.trimToEmpty(this.hostInputPane.getText());
			String port = StringUtils.trimToEmpty(this.portInputPane.getText());
			if (host.isEmpty() && port.isEmpty()) {
				this.hostInputPane.setText("127.0.0.1");
				FxAnimationUtil.makeEmptyText(this.portInputPane.getInputField(), 750);
			} else if (host.isEmpty()) {
				this.hostInputPane.setText("127.0.0.1");
			} else if (port.isEmpty()) {
				FxAnimationUtil.makeEmptyText(this.portInputPane.getInputField(), 750);
			} else {
				PlayerAccount account = this.client.getAccount();
				this.connectAndSend(host, Integer.valueOf(port), new ClientJoinPacket(account.getName(), account.getUUID()));
			}
		} else {
			if (this.client.getLoginWindow() == null)  {
				LoginWindow window = new LoginWindow(this.client, new Stage());
				window.show();
			}
		}
	}
	
	protected void connectAndSend(String host, int port, Packet<?> packet) {
		ConnectionHandler handler = this.client.getServerHandler();
		try {
			handler.connect(host, port);
			Util.runDelayed("DelayedPacketSender", 1000, () -> {
				Connection connection = handler.getConnection();
				if (handler.isConnected()) {
					connection.send(packet);
				} else {
					LOGGER.warn("Unable to send Packet of type {} to virtual game collection server, since connection is closed", packet.getClass().getSimpleName());
				}
			});
		} catch (Exception e) {
			LOGGER.warn("Fail to connect to virtual game collection server", e);
		}
	}
	
	protected void handleBack() {
		this.showScreen(this.backScreen);
	}
	
	@Override
	protected Pane createPane() {
		GridPane outerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		GridPane innerPane = FxUtil.makeGrid(Pos.CENTER, 10.0, 20.0);
		innerPane.addColumn(0, this.connectButtonBox, this.backButtonBox);
		outerPane.addColumn(0, this.hostInputPane, this.portInputPane, innerPane);
		return outerPane;
	}

}
