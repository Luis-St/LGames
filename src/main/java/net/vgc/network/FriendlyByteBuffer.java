package net.vgc.network;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.vgc.account.PlayerAccount;
import net.vgc.game.ttt.TTTType;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.game.ttt.map.TTTResultLine;
import net.vgc.player.GameProfile;
import net.vgc.util.Util;

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
		UUID uuid = new UUID(most, least);
		return uuid.equals(Util.EMPTY_UUID) ? Util.EMPTY_UUID : uuid;
	}
	
	public void writeAccount(PlayerAccount value) {
		value.write(this);
	}
	
	public PlayerAccount readAccount() {
		String name = this.readString();
		String password = this.readString();
		UUID uuid = this.readUUID();
		boolean guest = this.readBoolean();
		PlayerAccount account = new PlayerAccount(name, password, uuid, guest);
		return account.equals(PlayerAccount.UNKNOWN) ? PlayerAccount.UNKNOWN : account;
	}
	
	public void writeGameProfile(GameProfile value) {
		this.writeString(value.getName());
		this.writeUUID(value.getUUID());
	}
	
	public GameProfile readGameProfile() {
		String name = this.readString();
		UUID uuid = this.readUUID();
		GameProfile gameProfile = new GameProfile(name, uuid);
		return gameProfile.equals(GameProfile.EMPTY) ? GameProfile.EMPTY : gameProfile;
	}
	
	public void writeTTTType(TTTType value) {
		this.writeInt(value.getId());
	}
	
	public TTTType readTTTType() {
		int id = this.readInt();
		return TTTType.fromId(id);
	}
	
	public void writeTTTMap(TTTMap value) {
		this.writeTTTType(value.getTopLeftType());
		this.writeTTTType(value.getTopCenterType());
		this.writeTTTType(value.getTopRightType());
		this.writeTTTType(value.getMidLeftType());
		this.writeTTTType(value.getMidCenterType());
		this.writeTTTType(value.getMidRightType());
		this.writeTTTType(value.getBottomLeftType());
		this.writeTTTType(value.getBottomCenterType());
		this.writeTTTType(value.getBottomRightType());
	}
	
	public TTTMap readTTTMap() {
		TTTType topLeftType = this.readTTTType();
		TTTType topCenterType = this.readTTTType();
		TTTType topRightType = this.readTTTType();
		TTTType midLeftType = this.readTTTType();
		TTTType midCenterType = this.readTTTType();
		TTTType midRightType = this.readTTTType();
		TTTType bottomLeftType = this.readTTTType();
		TTTType bottomCenterType = this.readTTTType();
		TTTType bottomRightType = this.readTTTType();
		return new TTTMap(topLeftType, topCenterType, topRightType, midLeftType, midCenterType, midRightType, bottomLeftType, bottomCenterType, bottomRightType);
	}
	
	public void writeTTTResultLine(TTTResultLine value) {
		this.writeInt(value.getType().getId());
		this.writeInt(value.getVMap0());
		this.writeInt(value.getHMap0());
		this.writeInt(value.getVMap1());
		this.writeInt(value.getHMap1());
		this.writeInt(value.getVMap2());
		this.writeInt(value.getHMap2());
	}
	
	public TTTResultLine readTTTResultLine() {
		TTTType state = TTTType.fromId(this.readInt());
		int vMap0 = this.readInt();
		int hMap0 = this.readInt();
		int vMap1 = this.readInt();
		int hMap1 = this.readInt();
		int vMap2 = this.readInt();
		int hMap2 = this.readInt();
		TTTResultLine resultLine = new TTTResultLine(state, vMap0, hMap0, vMap1, hMap1, vMap2, hMap2);
		return resultLine.equals(TTTResultLine.EMPTY) ? TTTResultLine.EMPTY : resultLine;
	}
	
	public ByteBuf toBuffer() {
		return this.buffer;
	}
	
}
