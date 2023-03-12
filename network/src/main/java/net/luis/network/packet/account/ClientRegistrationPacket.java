package net.luis.network.packet.account;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Luis-st
 *
 */

public class ClientRegistrationPacket implements AccountPacket {
	
	private final String name;
	private final String mail;
	private final int passwordHash;
	private final String firstName;
	private final String lastName;
	private final Date birthday;
	
	public ClientRegistrationPacket(@NotNull String name, @NotNull String mail, int passwordHash, @NotNull String firstName, @NotNull String lastName, @NotNull Date birthday) {
		this.name = name;
		this.mail = mail;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
	}
	
	public ClientRegistrationPacket(@NotNull FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.mail = buffer.readString();
		this.passwordHash = buffer.readInt();
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
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeString(this.mail);
		buffer.writeInt(this.passwordHash);
		buffer.writeString(this.firstName);
		buffer.writeString(this.lastName);
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.birthday);
			buffer.writeInt(calendar.get(Calendar.YEAR));
			buffer.writeInt(calendar.get(Calendar.MONTH));
			buffer.writeInt(calendar.get(Calendar.DAY_OF_MONTH));
		}
	}
	
	@PacketGetter
	public @NotNull String getName() {
		return this.name;
	}
	
	@PacketGetter
	public @NotNull String getMail() {
		return this.mail;
	}
	
	@PacketGetter
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
	@PacketGetter
	public @NotNull String getFirstName() {
		return this.firstName;
	}
	
	@PacketGetter
	public @NotNull String getLastName() {
		return this.lastName;
	}
	
	@PacketGetter
	public @NotNull Date getBirthday() {
		return this.birthday;
	}
	
}
