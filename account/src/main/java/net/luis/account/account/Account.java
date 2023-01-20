package net.luis.account.account;

import javafx.scene.control.TreeItem;
import net.luis.language.TranslationKey;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.TagUtils;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.ToString;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class Account implements Encodable, Serializable {
	
	public static final Account UNKNOWN = new Account("Unknown", "unknown".hashCode(), 0, Utils.EMPTY_UUID, "", "Unknown", "Unknown", new Date(), AccountType.UNKNOWN);
	public static final Account TEST_1 = new Account("Test 1", "1".hashCode(), 1, UUID.fromString("11111111-1111-1111-1111-111111111111"), "", "Test", "1", new Date(), AccountType.TEST);
	public static final Account TEST_2 = new Account("Test 2", "2".hashCode(), 2, UUID.fromString("22222222-2222-2222-2222-222222222222"), "", "Test", "2", new Date(), AccountType.TEST);
	public static final Account TEST_3 = new Account("Test 3", "3".hashCode(), 3, UUID.fromString("33333333-3333-3333-3333-333333333333"), "", "Test", "3", new Date(), AccountType.TEST);
	public static final Account TEST_4 = new Account("Test 4", "4".hashCode(), 4, UUID.fromString("44444444-4444-4444-4444-444444444444"), "", "Test", "4", new Date(), AccountType.TEST);
	private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private final String name;
	private final int passwordHash;
	private final int id;
	private final UUID uuid;
	private final String mail;
	private final String firstName;
	private final String lastName;
	private final Date birthday;
	private final AccountType type;
	private boolean taken;
	
	Account(String name, int passwordHash, int id, UUID uuid, String mail, String firstName, String lastName, Date birthday, AccountType type) {
		this.name = name;
		this.passwordHash = passwordHash;
		this.id = id;
		this.uuid = uuid;
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.type = type;
	}
	
	@DecodingConstructor
	private Account(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.passwordHash = buffer.readInt();
		this.id = buffer.readInt();
		this.uuid = buffer.readUUID();
		this.mail = buffer.readString();
		this.firstName = buffer.readString();
		this.lastName = buffer.readString();
		{
			Calendar calendar = Calendar.getInstance();
			int year = buffer.readInt();
			int month = buffer.readInt();
			int day = buffer.readInt();
			calendar.set(year, month, day);
			this.birthday = calendar.getTime();
		}
		this.type = buffer.readEnum(AccountType.class);
	}
	
	public Account(@NotNull CompoundTag tag) {
		this.name = tag.getString("Name");
		this.passwordHash = tag.getInt("PasswordHash");
		this.id = tag.getInt("Id");
		this.uuid = TagUtils.readUUID(tag.getCompound("UUID"));
		this.mail = tag.getString("Mail");
		this.firstName = tag.getString("FirstName");
		this.lastName = tag.getString("LastName");
		{
			Calendar calendar = Calendar.getInstance();
			CompoundTag birthdayTag = tag.getCompound("Birthday");
			int year = birthdayTag.getInt("BirthdayYear");
			int month = birthdayTag.getInt("BirthdayMonth");
			int day = birthdayTag.getInt("BirthdayDay");
			calendar.set(year, month, day);
			this.birthday = calendar.getTime();
		}
		this.type = AccountType.values()[tag.getInt("Type")];
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
	public int getId() {
		return this.id;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public String getMail() {
		return this.mail;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public Date getBirthday() {
		return this.birthday;
	}
	
	public AccountType getType() {
		return this.type;
	}
	
	public boolean isTaken() {
		return this.taken;
	}
	
	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	
	public TreeItem<String> display() {
		TreeItem<String> rootItem = new TreeItem<>(TranslationKey.createAndGet("account.window.account", this.name + "#" + this.getDisplayId()));
		TreeItem<String> userDataItem = new TreeItem<>(TranslationKey.createAndGet("account.window.user_data"));
		userDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.name", this.name)));
		if (!StringUtils.isEmpty(this.firstName.trim())) {
			userDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.first_name", this.firstName)));
		}
		if (!StringUtils.isEmpty(this.lastName.trim())) {
			userDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.last_name", this.lastName)));
		}
		if (!StringUtils.isEmpty(this.mail.trim())) {
			userDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.mail", this.mail)));
		}
		userDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.birthday", FORMAT.format(this.birthday))));
		TreeItem<String> systemDataItem = new TreeItem<>(TranslationKey.createAndGet("account.window.system_data"));
		systemDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.id", this.getDisplayId())));
		systemDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.uuid", this.uuid)));
		systemDataItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("account.window.type", this.type.getTranslation())));
		rootItem.getChildren().add(userDataItem);
		rootItem.getChildren().add(systemDataItem);
		return rootItem;
	}
	
	private @NotNull String getDisplayId() {
		String id = String.valueOf(this.id);
		if (id.length() == 4) {
			return id;
		}
		return "0".repeat(4 - id.length()) + id;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeInt(this.passwordHash);
		buffer.writeInt(this.id);
		buffer.writeUUID(this.uuid);
		buffer.writeString(this.mail);
		buffer.writeString(this.firstName);
		buffer.writeString(this.lastName);
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.birthday);
			buffer.writeInt(calendar.get(Calendar.YEAR));
			buffer.writeInt(calendar.get(Calendar.MONTH));
			buffer.writeInt(calendar.get(Calendar.DAY_OF_MONTH));
		}
		buffer.writeEnum(this.type);
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Name", this.name);
		tag.putInt("PasswordHash", this.passwordHash);
		tag.putInt("Id", this.id);
		tag.put("UUID", TagUtils.writeUUID(this.uuid));
		tag.putString("Mail", this.mail);
		tag.putString("FirstName", this.firstName);
		tag.putString("LastName", this.lastName);
		{
			CompoundTag birthdayTag = new CompoundTag();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.birthday);
			birthdayTag.putInt("BirthdayYear", calendar.get(Calendar.YEAR));
			birthdayTag.putInt("BirthdayMonth", calendar.get(Calendar.MONTH));
			birthdayTag.putInt("BirthdayDay", calendar.get(Calendar.DAY_OF_MONTH));
			tag.put("Birthday", birthdayTag);
		}
		tag.putInt("Type", this.type.ordinal());
		return tag;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Account account)) return false;
		
		if (this.passwordHash != account.passwordHash) return false;
		if (this.id != account.id) return false;
		if (this.taken != account.taken) return false;
		if (!this.name.equals(account.name)) return false;
		if (!this.uuid.equals(account.uuid)) return false;
		if (!this.mail.equals(account.mail)) return false;
		if (!this.firstName.equals(account.firstName)) return false;
		if (!this.lastName.equals(account.lastName)) return false;
		if (!this.birthday.equals(account.birthday)) return false;
		return this.type == account.type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.passwordHash, this.id, this.uuid, this.mail, this.firstName, this.lastName, this.birthday, this.type, this.taken);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	public String toShortString() {
		return this.name + "#" + this.id;
	}
	
}
