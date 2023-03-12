package net.luis.account;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.luis.Constants;
import net.luis.account.account.Account;
import net.luis.account.account.AccountAgent;
import net.luis.account.account.AccountType;
import net.luis.account.window.AccountCreationWindow;
import net.luis.fx.screen.AbstractScreen;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.PropertyListeners;
import net.luis.game.application.FxApplication;
import net.luis.language.TranslationKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

class MainScreen extends AbstractScreen {
	
	private static final Logger LOGGER = LogManager.getLogger(MainScreen.class);
	
	private final AccountServer accountServer = AccountServer.getInstance();
	private TreeView<String> accountView;
	private GridPane buttonPane;
	
	public MainScreen() {
		super(TranslationKey.createAndGet("account.constans.name"), 450, 400, false);
	}
	
	@Override
	public void init() {
		this.accountView = new TreeView<>();
		this.accountView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("account.window.accounts"));
		for (Account account : this.accountServer.getAccountAgent()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constants.DEBUG_MODE);
		this.buttonPane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button createAccountButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.create"), this::createAccount);
		createAccountButton.setPrefWidth(110.0);
		Button removeAccountButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.remove"), this::removeAccount);
		removeAccountButton.setDisable(true);
		removeAccountButton.setPrefWidth(110.0);
		Button refreshButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refresh);
		refreshButton.setPrefWidth(110.0);
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), this.accountServer::exit);
		closeButton.setPrefWidth(110.0);
		this.buttonPane.addRow(0, createAccountButton, removeAccountButton, refreshButton, closeButton);
		this.accountView.getSelectionModel().selectedItemProperty().addListener(PropertyListeners.create((oldValue, newValue) -> {
			if (newValue == null) {
				removeAccountButton.setDisable(true);
			} else if (this.accountView.getRoot() != newValue.getParent()) {
				removeAccountButton.setDisable(true);
			} else {
				Account account = this.accountServer.getAccountAgent().getAccounts().get(this.accountView.getRoot().getChildren().indexOf(newValue));
				removeAccountButton.setDisable(account.getType() == AccountType.TEST || account.isTaken());
			}
		}));
	}
	
	private void createAccount() {
		new AccountCreationWindow(new Stage()).show();
	}
	
	private void removeAccount() {
		TreeItem<String> selectedItem = this.accountView.getSelectionModel().getSelectedItem();
		AccountAgent manager = this.accountServer.getAccountAgent();
		if (this.accountView.getRoot() == selectedItem.getParent()) {
			Account account = manager.getAccounts().get(this.accountView.getRoot().getChildren().indexOf(selectedItem));
			if (account.getType() == AccountType.TEST) {
				LOGGER.warn("Can not remove a test account");
			} else {
				manager.removeAccount(account.getName().hashCode(), account.getPasswordHash());
				this.refresh();
			}
		}
	}
	
	@Override
	public void refresh() {
		TreeItem<String> treeItem = new TreeItem<>();
		for (Account account : this.accountServer.getAccountAgent()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constants.DEBUG_MODE);
	}
	
	@Override
	protected @NotNull Pane createPane() {
		return FxUtils.makeDefaultVBox(this.accountView, this.buttonPane);
	}
}
