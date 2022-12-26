package net.vgc.account.account;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javafx.scene.control.TreeItem;
import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.TagUtils;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.Equals;
import net.luis.utils.util.ToString;
import net.vgc.language.TranslationKey;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.EnumRepresentable;
import net.vgc.util.Util;
import net.vgc.util.annotation.DecodingConstructor;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class Account implements Encodable, Serializable {
	
	public static final Account UNKNOWN = new Account("Unknown", "unknown".hashCode(), 0000, Util.EMPTY_UUID, "unknown@vgc.net", "Unknown", "Unknown", new Date(), AccountType.UNKNOWN);
	public static final Account TEST_1 = new Account("Test 1", "1".hashCode(), 0001, UUID.fromString("11111111-1111-1111-1111-111111111111"), "test@vgc.net", "Unknown", "Test", new Date(), AccountType.TEST);
	public static final Account TEST_2 = new Account("Test 2", "2".hashCode(), 0002, UUID.fromString("22222222-2222-2222-2222-222222222222"), "test@vgc.net", "Unknown", "Test", new Date(), AccountType.TEST);
	public static final Account TEST_3 = new Account("Test 3", "3".hashCode(), 0003, UUID.fromString("33333333-3333-3333-3333-333333333333"), "test@vgc.net", "Unknown", "Test", new Date(), AccountType.TEST);
	public static final Account TEST_4 = new Account("Test 4", "4".hashCode(), 0004, UUID.fromString("44444444-4444-4444-4444-000000000000"), "test@vgc.net", "Unknown", "Test", new Date(), AccountType.TEST);
	
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
	
	public Account(CompoundTag tag) {
		this.name = tag.getCryptString("Name");
		this.passwordHash = tag.getInt("PasswordHash");
		this.id = tag.getInt("Id");
		this.uuid = TagUtils.readUUID(tag.getCompound("UUID"));
		this.mail = tag.getCryptString("Mail");
		this.firstName = tag.getCryptString("FirstName");
		this.lastName = tag.getCryptString("LastName");
		{
			Calendar calendar = Calendar.getInstance();
			CompoundTag birthdayTag = tag.getCompound("Birthday");
			int year = birthdayTag.getInt("BirthdayYear");
			int month = birthdayTag.getInt("BirthdayMonth");
			int day = birthdayTag.getInt("BirthdayDay");
			calendar.set(year, month, day);
			this.birthday = calendar.getTime();
		}
		this.type = EnumRepresentable.fromId(AccountType.class, tag.getInt("Type"));
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
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("account.window.account", this.name));
		// treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_name", this.name)));
		// treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_password", this.password)));
		// treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_uuid", this.uuid)));
		// String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		// String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		// treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_guest", this.guest ? trueTranslation : falseTranslation)));
		// treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("account.window.account_taken", this.taken ? trueTranslation : falseTranslation)));
		return treeItem;
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
		tag.putCryptString("Name", this.name);
		tag.putInt("PasswordHash", this.passwordHash);
		tag.putInt("Id", this.id);
		tag.put("UUID", TagUtils.writeUUID(this.uuid));
		tag.putCryptString("Mail", this.mail);
		tag.putCryptString("FirstName", this.firstName);
		tag.putCryptString("LastName", this.lastName);
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
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	public String toShortString() {
		return this.name + "#" + this.id;
	}
	
}
