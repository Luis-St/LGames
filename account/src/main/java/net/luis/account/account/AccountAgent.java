package net.luis.account.account;

import com.google.common.collect.Lists;
import net.luis.Constants;
import net.luis.account.AccountServer;
import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.serialization.SerializationUtils;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.collection.ListTag;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class AccountAgent implements Serializable, Iterable<Account> {
	
	private static final Logger LOGGER = LogManager.getLogger(AccountAgent.class);
	private static final List<Account> TEST_ACCOUNTS = Utils.make(Lists.newArrayList(), (list) -> {
		list.add(Account.TEST_1);
		list.add(Account.TEST_2);
		list.add(Account.TEST_3);
		list.add(Account.TEST_4);
	});
	
	private List<Account> accounts;
	
	public AccountAgent() {
		this.accounts = Lists.newArrayList();
	}
	
	//region IO
	public AccountAgent(@NotNull CompoundTag tag) {
		if (!tag.contains("Accounts", Tag.COMPOUND_TAG)) {
			throw new IllegalArgumentException("Unable to deserialize AccountManager, since the tag does not contain the key 'Accounts'");
		}
		ListTag accountsTag = tag.getList("Accounts", Tag.COMPOUND_TAG);
		if (accountsTag.isEmpty()) {
			LOGGER.info("No accounts were found in the saved file");
			this.accounts = Lists.newArrayList();
		}
		List<Account> accounts = Lists.newArrayList();
		for (Tag accountTag : accountsTag) {
			if (accountTag instanceof CompoundTag) {
				Account account = SerializationUtils.deserialize(Account.class, (CompoundTag) accountTag);
				LOGGER.info("Account {} has been successfully loaded", account.toShortString());
				accounts.add(account);
			} else {
				throw new IllegalArgumentException("Unable to deserialize account from tag because the tag is an invalid type");
			}
		}
		LOGGER.info("Load {} accounts successfully", accounts.size());
		if (Constants.DEV_MODE) {
			accounts.addAll(TEST_ACCOUNTS);
		}
		this.accounts = accounts;
	}
	//endregion
	
	private static @NotNull UUID generateUUID(int nameHash, int passwordHash) {
		if (Constants.DEV_MODE) {
			Optional<Account> optional = TEST_ACCOUNTS.stream().filter(account -> account.getName().hashCode() == nameHash && account.getPasswordHash() == passwordHash).findAny();
			if (optional.isPresent()) {
				return optional.get().getUUID();
			}
		}
		return new UUID(nameHash, passwordHash);
	}
	
	public @NotNull List<Account> getAccounts() {
		return this.accounts;
	}
	
	@Override
	public @NotNull Iterator<Account> iterator() {
		return this.accounts.iterator();
	}
	
	private @Nullable Account findAccount(@NotNull UUID uuid) {
		if (Constants.DEV_MODE && Constants.DEBUG_MODE) {
			Optional<Account> optional = TEST_ACCOUNTS.stream().filter(account -> account.getUUID().equals(uuid)).findAny();
			if (optional.isPresent()) {
				return optional.get();
			}
		}
		for (Account account : this.accounts) {
			if (account.getUUID().equals(uuid)) {
				return account;
			}
		}
		return null;
	}
	
	private void refresh() {
		AccountServer.getInstance().getScreen().refresh();
	}
	
	public @NotNull Account createAccount(@NotNull String name, @NotNull String mail, int passwordHash, @NotNull String firstName, @NotNull String lastName, @NotNull Date birthday, @NotNull AccountType type) {
		Account account = new Account(name, passwordHash, Mth.randomInclusiveInt(new Random(), 1, 9999), generateUUID(name.hashCode(), passwordHash), mail, firstName, lastName, birthday, type);
		if (this.findAccount(generateUUID(name.hashCode(), passwordHash)) == null) {
			LOGGER.info("Account {} was created successfully", account.toShortString());
			this.accounts.add(account);
			this.refresh();
			return account;
		}
		LOGGER.error("Failed to create an account for user {} because an account with those credentials already exists", name);
		return Account.UNKNOWN;
	}
	
	public @NotNull Account createAndLogin(@NotNull String name, @NotNull String mail, int passwordHash, @NotNull String firstName, @NotNull String lastName, @NotNull Date birthday, @NotNull AccountType type) {
		Account account = this.createAccount(name, mail, passwordHash, firstName, lastName, birthday, type);
		LOGGER.info("Client logged in with account {}", account.toShortString());
		account.setTaken(true);
		return account;
	}
	
	public @NotNull Account accountLogin(@NotNull String name, int passwordHash) {
		Account account = this.findAccount(generateUUID(name.hashCode(), passwordHash));
		if (account != null) {
			if (account.isTaken()) {
				LOGGER.warn("Unable to login because the account {} is already being used by another player", account.toShortString());
				return Account.UNKNOWN;
			} else if (account.getPasswordHash() == passwordHash) {
				LOGGER.info("Client logged in with account {}", account.toShortString());
				account.setTaken(true);
				this.refresh();
				return account;
			} else {
				LOGGER.error("Could not log in to account {} because credentials do not match", account.toShortString());
				return Account.UNKNOWN;
			}
		}
		LOGGER.error("Failed to log in because there is no account for user {}", name);
		return Account.UNKNOWN;
	}
	
	public boolean accountLogout(@NotNull String name, int id, @NotNull UUID uuid) {
		Account account = this.findAccount(uuid);
		if (account != null) {
			if (!account.isTaken()) {
				LOGGER.warn("Failed to log out because the account {} is not being used by a player", account.toShortString());
				return false;
			} else if (account.getName().hashCode() == name.hashCode() && account.getId() == id) {
				LOGGER.info("Client logged out with account {}", account.toShortString());
				account.setTaken(false);
				this.refresh();
				return true;
			} else {
				LOGGER.error("Failed to log out on account {} because credentials do not match", account.toShortString());
				return false;
			}
		}
		LOGGER.warn("Failed to log out because there is no such account {}#{}", name, id);
		return false;
	}
	
	public boolean removeAccount(int nameHash, int passwordHash) {
		Account account = this.findAccount(generateUUID(nameHash, passwordHash));
		if (account != null) {
			if (!account.isTaken()) {
				LOGGER.info("Account {} has been successfully removed", account.toShortString());
				return this.accounts.remove(account);
			}
			LOGGER.warn("Unable to remove account {} because the account is currently being used by a player", account.toShortString());
			return false;
		}
		LOGGER.error("Could not remove account with uuid {} because it does not exist", generateUUID(nameHash, passwordHash));
		return false;
	}
	
	public void close() {
		this.accounts.clear();
	}
	
	//region IO
	@Override
	public @NotNull CompoundTag serialize() {
		List<Account> accounts = Lists.newArrayList(this.accounts);
		LOGGER.info("Removed {} single session accounts", accounts.stream().filter(Account::isSingleSession).count());
		accounts.removeIf(Account::isSingleSession);
		CompoundTag tag = new CompoundTag();
		ListTag accountsTag = new ListTag();
		for (Account account : accounts) {
			accountsTag.add(account.serialize());
		}
		tag.put("Accounts", accountsTag);
		LOGGER.info("Saved {} accounts successfully", accountsTag.size());
		return tag;
	}
	//endregion
}
