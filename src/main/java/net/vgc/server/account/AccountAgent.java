package net.vgc.server.account;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.common.InfoResult;
import net.vgc.common.Result;

public final class AccountAgent {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final List<PlayerAccount> accounts;
	
	public AccountAgent(List<PlayerAccount> accounts) {
		this.accounts = accounts;
	}
	
	public List<PlayerAccount> getAccounts() {
		return this.accounts;
	}
	
	public PlayerAccount createAccount(String name, String password, UUID uuid, boolean guest) {
		PlayerAccount account = new PlayerAccount(name, password, uuid, guest);
		this.accounts.add(account);
		return account;
	}
	
	public PlayerAccount createGuestAccount(String name, String password, UUID uuid) {
		return this.createAccount(name, password, uuid, true);
	}
	
	public PlayerAccountInfo accountLogin(String name, String password) {
		List<PlayerAccount> accounts = Lists.newArrayList(this.accounts);
		accounts.removeIf((account) -> {
			return !account.match(name, password);
		});
		if (accounts.isEmpty()) {
			LOGGER.warn("Fail to login, since there is no Account with the following account data: name {} password {}", name, password);
			return new PlayerAccountInfo(new InfoResult(Result.FAILED, "There is no Account which match with the given account data!"), PlayerAccount.UNKNOWN);
		} if (accounts.size() == 1) {
			PlayerAccount account = accounts.get(0);
			if (account.isTaken()) {
				LOGGER.warn("Fail to login, since the Account {} is already used by another player", account.toString().replace("PlayerAccount", ""));
				return new PlayerAccountInfo(new InfoResult(Result.FAILED, "Account is already used by another player!"), PlayerAccount.UNKNOWN);
			} else if (this.accounts.remove(account)) {
				account.setTaken(true);
				this.accounts.add(account); 
				return new PlayerAccountInfo(new InfoResult(Result.SUCCESS, "Successfully logged in"), account);
			}
		}
		List<String> accountStrings = accounts.stream().map(PlayerAccount::toString).map((string) -> { 
			return string.replace("PlayerAccount", "").replace("[", "{").replace("]", "}");
		}).toList();
		LOGGER.warn("Fail to login, since the following Accounts {} match with the following account data: name {} password {}", accountStrings.toString(), name, password);
		return new PlayerAccountInfo(new InfoResult(Result.FAILED, "There was an error checking your account. Please try again later!"), PlayerAccount.UNKNOWN);
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
				return new InfoResult(Result.SUCCESS, "Successfully logged out");
			}
			LOGGER.warn("Fail to logout, since the Account is not used by a player");
			return new InfoResult(Result.FAILED, "The Account which should be logged out is not used by a player");
		}
		LOGGER.warn("Fail to logout, since there is no Account with the following account data: name {} password {}", name, password);
		return new InfoResult(Result.FAILED, "There is no Account which match with the given account data!");
	}
	
	public void close() {
		this.accounts.clear();
	}

}
