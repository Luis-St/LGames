package net.vgc.server.account;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.common.InfoResult;
import net.vgc.common.Result;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;

public final class AccountAgent {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final List<PlayerAccount> accounts;
	
	public AccountAgent(List<PlayerAccount> accounts) {
		this.accounts = accounts;
	}
	
	public List<PlayerAccount> getAccounts() {
		return this.accounts;
	}
	
	protected PlayerAccount createAccount(String name, String password, UUID uuid, boolean guest) {
		PlayerAccount account = new PlayerAccount(name, password, uuid, guest);
		this.accounts.add(account);
		return account;
	}
	
	public PlayerAccount createUserAccount(String name, String password, UUID uuid) {
		return this.createAccount(name, this.checkOrCreatePassword(password), uuid, false);
	}
	
	public PlayerAccount createGuestAccount(String name, UUID uuid) {
		return this.createAccount(name, this.checkOrCreatePassword(null), uuid, true);
	}
	
	protected String checkOrCreatePassword(String password) {
		if (password != null && !password.trim().isEmpty()) {
			return password;
		}
		SecureRandom rng = new SecureRandom();
		return String.valueOf(rng.nextInt(100000));
	}
	
	public PlayerAccountInfo accountLogin(String name, String password) {
		List<PlayerAccount> accounts = Lists.newArrayList(this.accounts);
		accounts.removeIf((account) -> {
			return !account.match(name, password);
		});
		if (accounts.isEmpty()) {
			LOGGER.warn("Fail to login, since there is no account with the following account data: name {} password {}", name, password);
			return new PlayerAccountInfo(new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.login.no")), PlayerAccount.UNKNOWN);
		} if (accounts.size() == 1) {
			PlayerAccount account = accounts.get(0);
			if (account.isTaken()) {
				LOGGER.warn("Fail to login, since the account {} is already used by another player", account.toString().replace("PlayerAccount", ""));
				return new PlayerAccountInfo(new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.login.taken")), PlayerAccount.UNKNOWN);
			} else if (this.accounts.remove(account)) {
				account.setTaken(true);
				this.accounts.add(account); 
				return new PlayerAccountInfo(new InfoResult(Result.SUCCESS, TranslationKey.createAndGet(Languages.EN_US, "account.login.successfully")), account);
			}
		}
		List<String> accountStrings = accounts.stream().map(PlayerAccount::toString).map((string) -> { 
			return string.replace("PlayerAccount", "").replace("[", "{").replace("]", "}");
		}).toList();
		LOGGER.warn("Fail to login, since the following accounts {} match with the following account data: name {} password {}", accountStrings.toString(), name, password);
		return new PlayerAccountInfo(new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.login.error")), PlayerAccount.UNKNOWN);
	}
	
	@Nullable
	protected PlayerAccount takeAccount(String name, String password, UUID uuid, boolean guest) {
		PlayerAccount account = this.accounts.get(this.accounts.indexOf(new PlayerAccount(name, password, uuid, guest)));
		if (account != null && this.accounts.remove(account)) {
			return account;
		}
		return null;
	}
	
	public InfoResult accountLogout(String name, String password, UUID uuid, boolean guest) {
		PlayerAccount account = this.takeAccount(name, password, uuid, guest);
		if (account != null) {
			if (account.isTaken()) {
				account.setTaken(false);
				this.accounts.add(account);
				return new InfoResult(Result.SUCCESS, TranslationKey.createAndGet(Languages.EN_US, "account.logout.successfully"));
			}
			LOGGER.warn("Fail to logout, since the Account is not used by a player");
			return new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.logout.unused"));
		}
		LOGGER.warn("Fail to logout, since there is no Account with the following account data: name {} password {}", name, password);
		return new InfoResult(Result.FAILED, TranslationKey.createAndGet(Languages.EN_US, "account.login.no"));
	}
	
	public void close() {
		this.accounts.clear();
	}

}
