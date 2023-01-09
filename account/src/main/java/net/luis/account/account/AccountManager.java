package net.luis.account.account;

import net.luis.Constants;
import net.luis.account.AccountServer;
import net.luis.utils.math.Mth;
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

public record AccountManager(List<Account> accounts) {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public AccountManager(List<Account> accounts) {
		this.accounts = accounts;
		if (Constants.IDE) {
			this.accounts.add(Account.TEST_1);
			this.accounts.add(Account.TEST_2);
			this.accounts.add(Account.TEST_3);
			this.accounts.add(Account.TEST_4);
		}
	}
	
	public static UUID generateUUID(int nameHash, int passwordHash) {
		if (Constants.IDE) {
			if (Account.TEST_1.getName().hashCode() == nameHash && Account.TEST_1.getPasswordHash() == passwordHash) {
				return Account.TEST_1.getUUID();
			} else if (Account.TEST_2.getName().hashCode() == nameHash && Account.TEST_2.getPasswordHash() == passwordHash) {
				return Account.TEST_2.getUUID();
			} else if (Account.TEST_3.getName().hashCode() == nameHash && Account.TEST_3.getPasswordHash() == passwordHash) {
				return Account.TEST_3.getUUID();
			} else if (Account.TEST_4.getName().hashCode() == nameHash && Account.TEST_4.getPasswordHash() == passwordHash) {
				return Account.TEST_4.getUUID();
			}
		}
		return new UUID(nameHash, passwordHash);
	}
	
	
	private Account findAccount(UUID uuid) {
		if (Constants.IDE && Constants.DEBUG) {
			if (Account.TEST_1.getUUID().equals(uuid)) {
				return Account.TEST_1;
			} else if (Account.TEST_2.getUUID().equals(uuid)) {
				return Account.TEST_2;
			} else if (Account.TEST_3.getUUID().equals(uuid)) {
				return Account.TEST_3;
			} else if (Account.TEST_4.getUUID().equals(uuid)) {
				return Account.TEST_4;
			}
		}
		for (Account account : this.accounts) {
			if (account.getUUID().equals(uuid)) {
				return account;
			}
		}
		return null;
	}
	
	public boolean removeAccount(int nameHash, int passwordHash) {
		Account account = this.findAccount(generateUUID(nameHash, passwordHash));
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
		Account account = new Account(name, passwordHash, Mth.randomInclusiveInt(new Random(), 1, 9999), generateUUID(name.hashCode(), passwordHash), mail, firstName, lastName, birthday, type);
		if (this.findAccount(generateUUID(name.hashCode(), passwordHash)) == null) {
			LOGGER.info("Account {} was created successfully", account.toShortString());
			this.accounts.add(account);
			AccountServer.getInstance().refreshScene();
			return account;
		}
		LOGGER.warn("Fail to create account for user {}, since there is already a account with these credentials", name);
		return Account.UNKNOWN;
	}
	
	public Account createAndLogin(String name, String mail, int passwordHash, String firstName, String lastName, Date birthday, AccountType type) {
		Account account = this.createAccount(name, mail, passwordHash, firstName, lastName, birthday, type);
		LOGGER.info("Client logged in with account {}", account.toShortString());
		account.setTaken(true);
		return account;
	}
	
	public Account accountLogin(String name, int passwordHash) {
		Account account = this.findAccount(generateUUID(name.hashCode(), passwordHash));
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
	
	public boolean accountLogout(String name, int id, UUID uuid) {
		Account account = this.findAccount(uuid);
		if (account != null) {
			if (!account.isTaken()) {
				LOGGER.warn("Fail to logout, since the account is not used by a player");
				return false;
			} else if (account.getName().hashCode() == name.hashCode() && account.getId() == id) {
				LOGGER.info("Client logged out with account {}", account.toShortString());
				account.setTaken(false);
				AccountServer.getInstance().refreshScene();
				return true;
			} else {
				LOGGER.warn("Fail to logout to account {}, since credentials do not match", account.toShortString());
				return false;
			}
		}
		LOGGER.warn("Fail to logout, since there is no such account {}#{}", name, id);
		return false;
	}
	
	public void close() {
		this.accounts.clear();
	}
	
}
