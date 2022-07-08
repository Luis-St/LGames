package net.vgc.account;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AccountAgent {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final List<PlayerAccount> accounts;
	
	public AccountAgent(List<PlayerAccount> accounts) {
		this.accounts = accounts;
	}
	
	public List<PlayerAccount> getAccounts() {
		return this.accounts;
	}
	
	public boolean isPresent(UUID uuid) {
		return this.accounts.stream().map(PlayerAccount::getUUID).collect(Collectors.toList()).contains(uuid);
	}
	
	public PlayerAccount getAccount(UUID uuid) {
		if (this.isPresent(uuid)) {
			for (PlayerAccount account : this.accounts) {
				if (account.getUUID().equals(uuid)) {
					return account;
				}
			}
		}
		return PlayerAccount.UNKNOWN;
	}
	
	public boolean removeAccount(UUID uuid) {
		if (this.isPresent(uuid)) {
			PlayerAccount account = this.getAccount(uuid);
			if (!account.isTaken()) {
				LOGGER.info("Remove account: {}", account);
				return this.accounts.remove(account);
			}
			LOGGER.warn("Unable to remove account {}, since the account is currently used by a player", account);
			return false;
		}
		LOGGER.warn("Fail to remove account with uuid {}, since it does not exists", uuid);
		return false;
	}
	
	public void setTaken(UUID uuid, boolean taken) {
		if (this.isPresent(uuid)) {
			this.getAccount(uuid).setTaken(taken);
		}
	}
	
	private String checkOrGeneratePassword(String password, int length) {
		if (!StringUtils.trimToEmpty(password).isEmpty()) {
			return password;
		}
		Random rng = new Random(System.currentTimeMillis());
		String characters = "~_+-!#$%&0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		password = "";
		for (int i = 0; i < length; i++) {
			password += characters.charAt(rng.nextInt(characters.length()));
		}
		return password;
	}
	
	private UUID generateUUID(String name, String password) {
		Random rng = new Random(name.charAt(0) * password.charAt(0));
		long mostBits = 0;
		String most = name + password + name;
		for (int i = 0; i < most.length(); i++) {
			mostBits += most.charAt(i) * rng.nextInt(Math.max(1, i * i) * Math.max(1, i * i)) * rng.nextInt(Math.max(1, i * i) * Math.max(1, i * i));
		}
		long leastBits = 0;
		String least = password + name + password;
		for (int i = 0; i < least.length(); i++) {
			leastBits += least.charAt(i) * rng.nextInt(Math.max(1, i * i) * Math.max(1, i * i)) * rng.nextInt(Math.max(1, i * i) * Math.max(1, i * i));
		}
		return new UUID(mostBits + rng.nextLong(Math.max(1, leastBits)), leastBits + rng.nextLong(Math.max(1, mostBits)));
	}
	
	public PlayerAccount createAccount(String name, String password, boolean guest) {
		password = this.checkOrGeneratePassword(password, 15);
		PlayerAccount account = new PlayerAccount(name, password, this.generateUUID(name, password), guest);
		this.accounts.add(account);
		AccountServer.getInstance().refreshScene();
		return account;
	}
	
	public PlayerAccount createAndLogin(String name, String password, boolean guest) {
		PlayerAccount account = this.createAccount(name, password, guest);
		account.setTaken(true);
		AccountServer.getInstance().refreshScene();
		return account;
	}
	
	public PlayerAccount accountLogin(String name, String password) {
		UUID uuid = this.generateUUID(name, password);
		if (this.isPresent(uuid)) {
			PlayerAccount account = this.getAccount(uuid);
			if (account.isTaken()) {
				LOGGER.warn("Fail to login, since the account {} is already used by another player", account.toString().replace("PlayerAccount", ""));
				return PlayerAccount.UNKNOWN;
			} else {
				LOGGER.info("Client logged in with account: {}", account);
				this.setTaken(uuid, true);
				AccountServer.getInstance().refreshScene();
				return account;
			}
		}
		LOGGER.warn("Fail to login, since there is no account with uuid {} and account data: name {} password {}", uuid, name, password);
		return PlayerAccount.UNKNOWN;
	}
	
	public boolean accountLogout(String name, String password) {
		UUID uuid = this.generateUUID(name, password);
		if (this.isPresent(uuid)) {
			if (this.getAccount(uuid).isTaken()) {
				LOGGER.info("Client logged out with account: {}", this.getAccount(uuid));
				this.setTaken(uuid, false);
				AccountServer.getInstance().refreshScene();
				return true;
			} else {
				LOGGER.warn("Fail to logout, since the account is not used by a player");
				return false;
			}
		}
		LOGGER.warn("Fail to logout, since there is no account with uuid {} and account data: name {} password {}", uuid, name, password);
		return false;
	}
	
	public void close() {
		this.accounts.clear();
	}

}
