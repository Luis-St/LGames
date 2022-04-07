package net.vgc.server.account;

import java.util.UUID;

import net.vgc.Constans;
import net.vgc.data.serialization.Serializable;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.NetworkSide;
import net.vgc.util.Util;

public final class PlayerAccount implements Serializable {
	
	public static final PlayerAccount UNKNOWN = new PlayerAccount("Unknown", "unknown", Util.EMPTY_UUID, false);
	
	protected final String name;
	protected final String password;
	protected final UUID uuid;
	protected final boolean guest;
	protected boolean taken;
	
	public PlayerAccount(String name, String password, UUID uuid, boolean guest) {
		this.name = name;
		this.password = password;
		this.uuid = uuid;
		this.guest = guest;
	}
	
	public PlayerAccount(CompoundTag tag) {
		this.name = tag.getString("name");
		this.password = tag.getString("password");
		this.uuid = TagUtil.readUUID(tag.getCompound("uuid"));
		this.guest = tag.getBoolean("guest");
	}
	
	protected String obfuscated() {
		String obfuscated = "";
		for (int i = 0; i < this.password.length(); i++) {
			obfuscated += "?";
		}
		return obfuscated;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		if (NetworkSide.ACCOUNT_SERVER.isOn() || Constans.IDE || this == UNKNOWN) {
			return this.password;
		}
		return this.obfuscated();
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public boolean isGuest() {
		return this.guest;
	}
	
	public boolean isTaken() {
		return this.taken;
	}
	
	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	
	public boolean match(String name, String password) {
		return this.name.equals(name) && this.password.equals(password);
	}
	
	public boolean match(String name, String password, UUID uuid, boolean guest) {
		return this.name.equals(name) && this.password.equals(password) && this.uuid.equals(uuid) && this.guest == guest;
	}
	
	public void write(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeString(this.password);
		buffer.writeUUID(this.uuid);
		buffer.writeBoolean(this.guest);
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("name", this.name);
		tag.putString("password", this.password);
		tag.put("uuid", TagUtil.writeUUID(this.uuid));
		tag.putBoolean("guest", this.guest);
		return tag;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof PlayerAccount account) {
			return this.match(account.name, account.password, account.uuid, account.guest);
		} else if (object instanceof PlayerAccountInfo info) {
			PlayerAccount account = info.account();
			return this.match(account.name, account.password, account.uuid, account.guest);
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PlayerAccount[");
		builder.append("name=").append(this.name).append(",");
		if (NetworkSide.ACCOUNT_SERVER.isOn() || Constans.IDE || this == UNKNOWN) {
			builder.append("password=").append(this.password).append(",");
		} else {
			builder.append("password=").append(this.obfuscated()).append(",");
		}
		builder.append("uuid=").append(this.uuid).append(",");
		builder.append("guest=").append(this.guest).append("]");
		return builder.toString();
	}
	
}
