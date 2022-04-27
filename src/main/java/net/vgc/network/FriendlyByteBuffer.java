package net.vgc.network;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.vgc.account.PlayerAccount;
import net.vgc.account.PlayerAccountInfo;
import net.vgc.common.info.InfoResult;
import net.vgc.common.info.Result;
import net.vgc.player.GameProfile;

public class FriendlyByteBuffer {
	
	protected final ByteBuf buffer;
	
	public FriendlyByteBuffer() {
		this(Unpooled.buffer());
	}
	
	public FriendlyByteBuffer(ByteBuf buffer) {
		this.buffer = buffer;
	}
	
	public int readableBytes() {
		return this.buffer.readableBytes();
	}
	
	public int writerIndex() {
		return this.buffer.writerIndex();
	}
	
	public void writeByte(byte value) {
		this.buffer.writeByte(value);
	}
	
	public byte readByte() {
		return this.buffer.readByte();
	}
	
	public void writeShort(short value) {
		this.buffer.writeShort(value);
	}
	
	public short readShort() {
		return this.buffer.readShort();
	}
	
	public void writeInt(int value) {
		this.buffer.writeInt(value);
	}
	
	public int readInt() {
		return this.buffer.readInt();
	}
	
	public void writeLong(long value) {
		this.buffer.writeLong(value);
	}
	
	public long readLong() {
		return this.buffer.readLong();
	}
	
	public void writeFloat(float value) {
		this.buffer.writeFloat(value);
	}
	
	public float readFloat() {
		return this.buffer.readFloat();
	}
	
	public void writeDouble(double value) {
		this.buffer.writeDouble(value);
	}
	
	public double readDouble() {
		return this.buffer.readShort();
	}
	
	public void writeBoolean(boolean value) {
		this.buffer.writeBoolean(value);
	}
	
	public boolean readBoolean() {
		return this.buffer.readBoolean();
	}
	
	public void writeString(String value) {
		this.buffer.writeInt(value.length());
		this.buffer.writeCharSequence(value, StandardCharsets.UTF_8);
	}
	
	public String readString() {
		int length = this.buffer.readInt();
		return this.buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
	}
	
	public void writeUUID(UUID value) {
		this.writeLong(value.getMostSignificantBits());
		this.writeLong(value.getLeastSignificantBits());
	}
	
	public UUID readUUID() {
		long most = this.readLong();
		long least = this.readLong();
		return new UUID(most, least);
	}
	
	public void writeInfoResult(InfoResult value) {
		this.writeString(value.result().getName());
		this.writeString(value.info());
	}
	
	public InfoResult readInfoResult() {
		Result result = Result.fromName(this.readString());
		String info = this.readString();
		return new InfoResult(result, info);
	}
	
	public void writeAccount(PlayerAccount value) {
		value.write(this);
	}
	
	public PlayerAccount readAccount() {
		String name = this.readString();
		String password = this.readString();
		UUID uuid = this.readUUID();
		boolean guest = this.readBoolean();
		return new PlayerAccount(name, password, uuid, guest);
	}
	
	public void writeAccountInfo(PlayerAccountInfo value) {
		this.writeInfoResult(value.infoResult());
		this.writeAccount(value.account());
	}
	
	public PlayerAccountInfo readAccountInfo() {
		InfoResult infoResult = this.readInfoResult();
		PlayerAccount account = this.readAccount();
		return new PlayerAccountInfo(infoResult, account);
	}
	
	public void writeGameProfile(GameProfile value) {
		this.writeString(value.getName());
		this.writeUUID(value.getUUID());
	}
	
	public GameProfile readGameProfile() {
		String name = this.readString();
		UUID uuid = this.readUUID();
		return new GameProfile(name, uuid);
	}
	
	public ByteBuf toBuffer() {
		return this.buffer;
	}
	
}
