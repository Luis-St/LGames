package net.vgc.server.account;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

public final class AccountAgent {
	
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
	
	public PlayerAccountInfo getAccount(String name, String password) {
		List<PlayerAccount> accounts = Lists.newArrayList(this.accounts);
		accounts.removeIf((account) -> {
			return !account.match(name, password);
		});
		if (accounts.isEmpty()) {
			return new PlayerAccountInfo("There is no account with the name and password!", PlayerAccount.UNKNOWN);
		} if (accounts.size() == 1) {
			PlayerAccount account = accounts.get(0);
			if (account.isTaken()) {
				return new PlayerAccountInfo("Account is already in use by another player!", PlayerAccount.UNKNOWN);
			} else if (this.accounts.remove(account)) {
				account.setTaken(true);
				this.accounts.add(account);
				return new PlayerAccountInfo("", account);
			}
		}
		return new PlayerAccountInfo("There was an error checking your account. Please try again later!", PlayerAccount.UNKNOWN);
	}
	
	public void close() {
		this.accounts.clear();
	}

}
