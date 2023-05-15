package net.luis.client.account;

import net.luis.client.window.LoginWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class AccountManager {
	
	private static final Logger LOGGER = LogManager.getLogger(AccountManager.class);
	
	private LoginWindow loginWindow;
	private ClientAccount account;
	private boolean cachePasswordLocal;
	private String password;
	
	public @Nullable LoginWindow getLoginWindow() {
		return this.loginWindow;
	}
	
	public void setLoginWindow(@Nullable LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
	}
	
	public @Nullable ClientAccount getAccount() {
		return this.account;
	}
	
	public void login(@NotNull String name, int id, @NotNull String mail, @NotNull UUID uuid, boolean guest) {
		LOGGER.info("Login with account: {}#{}", name, id);
		this.account = new ClientAccount(name, id, mail, uuid, guest);
	}
	
	public boolean isLoggedIn() {
		return this.account != null;
	}
	
	public void logout() {
		this.account = null;
	}
	
	public void setCachePasswordLocal(boolean cachePasswordLocal) {
		this.cachePasswordLocal = cachePasswordLocal;
	}
	
	public boolean isPasswordCachedLocal() {
		return this.cachePasswordLocal;
	}
	
	public @Nullable String getPassword() {
		return this.password;
	}
	
	public void setPassword(@Nullable String password) {
		if (this.cachePasswordLocal) {
			this.password = password;
		} else {
			this.password = "";
			LOGGER.info("The password is not cached local because it is disabled");
		}
	}
}
