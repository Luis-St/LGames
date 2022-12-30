package net.vgc.account.account;

import net.luis.utils.math.Mth;
import net.vgc.account.AccountServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public final class AccountManager {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final List<Account> accounts;
	
	public AccountManager(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public static UUID generateUUID(int nameHash, int passwordHash) {
		return new UUID(nameHash, passwordHash);
	}
	
	public List<Account> getAccounts() {
		return this.accounts;
	}
	
	private Account findAccount(int nameHash, int passwordHash) {
		UUID uuid = generateUUID(nameHash, passwordHash);
		for (Account account : this.accounts) {
			if (account.getUUID().equals(uuid)) {
				return account;
			}
		}
		return null;
	}
	
	public boolean removeAccount(int nameHash, int passwordHash) {
		Account account = this.findAccount(nameHash, passwordHash);
		if (account != null) {
			if (!account.isTaken()) {
				LOGGER.info("Remove account: {}", account.toShortString());
				return this.accounts.remove(account);
			}
			LOGGER.warn("Unable to remove account {}, since the account is currently used by a player", account.toShortString());
			return false;
		}
		LOGGER.warn("Fail to remove account with uuid {}, since it does not exists", generateUUID(nameHash, passwordHash));
		return false;
	}
	
	public Account createAccount(String name, String mail, int passwordHash, String firstName, String lastName, Date birthday, AccountType type) {
		Account account = new Account(name, passwordHash, Mth.randomInclusiveInt(new Random(), 0, 9999), generateUUID(name.hashCode(), passwordHash), mail, firstName, lastName, birthday, type);
		if (this.findAccount(name.hashCode(), passwordHash) == null) {
			this.accounts.add(account);
			return account;
		}
		LOGGER.warn("Fail to create account for user {}, since there is already a account with these credentials", name);
		return Account.UNKNOWN;
	}
	
	public Account createAndLogin(String name, String mail, int passwordHash, String firstName, String lastName, Date birthday, AccountType type) {
		Account account = this.createAccount(name, mail, passwordHash, firstName, lastName, birthday, type);
		account.setTaken(true);
		AccountServer.getInstance().refreshScene();
		return account;
	}
	
	public Account accountLogin(String name, int passwordHash) {
		Account account = this.findAccount(name.hashCode(), passwordHash);
		if (account != null) {
			if (account.isTaken()) {
				LOGGER.warn("Fail to login, since the account {} is already used by another player", account.toShortString());
				return Account.UNKNOWN;
			} else if (account.getPasswordHash() == passwordHash) {
				LOGGER.info("Client logged in with account {}", account.toShortString());
				account.setTaken(true);
				AccountServer.getInstance().refreshScene();
				return account;
			} else {
				LOGGER.warn("Fail to login to account {}, since credentials do not match", account.toShortString());
				return Account.UNKNOWN;
			}
		}
		LOGGER.warn("Fail to login, since there is no account for user {}", name);
		return Account.UNKNOWN;
	}
	
	public boolean accountLogout(String name, int id, int passwordHash) {
		Account account = this.findAccount(name.hashCode(), passwordHash);
		if (account != null) {
			if (!account.isTaken()) {
				LOGGER.warn("Fail to logout, since the account is not used by a player");
				return false;
			} else if (account.getPasswordHash() == passwordHash) {
				LOGGER.info("Client logged out with account {}", account.toShortString());
				account.setTaken(false);
				AccountServer.getInstance().refreshScene();
				return true;
			} else {
				LOGGER.warn("Fail to logout to account {}, since credentials do not match", account.toShortString());
				return false;
			}
		}
		LOGGER.warn("Fail to logout, since there is no account with id {} and name {}", id, name);
		return false;
	}
	
	public void close() {
		this.accounts.clear();
	}
	
}
