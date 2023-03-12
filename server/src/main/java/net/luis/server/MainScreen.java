package net.luis.server;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.luis.Constants;
import net.luis.fx.screen.AbstractScreen;
import net.luis.fxutils.FxUtils;
import net.luis.game.player.Player;
import net.luis.language.TranslationKey;
import net.luis.server.player.ServerPlayer;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

class MainScreen extends AbstractScreen {
	
	private final Server server = Server.getInstance();
	private TreeView<String> serverInfoView;
	private TreeItem<String> playersTreeItem;
	private GridPane buttonPane;
	
	public MainScreen() {
		super(TranslationKey.createAndGet("server.constans.name"), 450, 400, false);
	}
	
	@Override
	public void init() {
		this.serverInfoView = new TreeView<>();
		TreeItem<String> rootItem = new TreeItem<>(TranslationKey.createAndGet("server.window.server"));
		rootItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_host", this.server.getHost())));
		rootItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_port", this.server.getPort())));
		rootItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_admin", this.server.getPlayerList().getAdminUUID())));
		this.playersTreeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.players"));
		for (Player player : this.server.getPlayerList().getPlayers()) {
			if (player instanceof ServerPlayer serverPlayer) {
				this.playersTreeItem.getChildren().add(serverPlayer.display());
			} else {
				throw new IllegalStateException("The player is not a ServerPlayer");
			}
		}
		rootItem.getChildren().add(this.playersTreeItem);
		this.serverInfoView.setRoot(rootItem);
		this.serverInfoView.setShowRoot(Constants.DEBUG_MODE);
		this.buttonPane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button settingsButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.menu.settings"), this::openSettings);
		settingsButton.setPrefWidth(150.0);
		settingsButton.setDisable(true);
		Button refreshButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refresh);
		refreshButton.setPrefWidth(Constants.DEV_MODE ? 150.0 : 225.0);
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), this.server::exit);
		closeButton.setPrefWidth(Constants.DEV_MODE ? 150.0 : 225.0);
		if (Constants.DEV_MODE) {
			this.buttonPane.addRow(0, settingsButton, refreshButton, closeButton);
		} else {
			this.buttonPane.addRow(0, refreshButton, closeButton);
		}
	}
	
	private void openSettings() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public void refresh() {
		this.playersTreeItem.getChildren().removeIf((string) -> true);
		for (Player player : this.server.getPlayerList().getPlayers()) {
			if (player instanceof ServerPlayer serverPlayer) {
				this.playersTreeItem.getChildren().add(serverPlayer.display());
			} else {
				throw new IllegalStateException("The player is not a ServerPlayer");
			}
		}
	}
	
	@Override
	protected @NotNull Pane createPane() {
		return FxUtils.makeDefaultVBox(this.serverInfoView, this.buttonPane);
	}
}
