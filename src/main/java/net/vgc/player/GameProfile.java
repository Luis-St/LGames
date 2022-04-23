package net.vgc.player;

import java.util.UUID;

import net.vgc.data.serialization.Serializable;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.util.Util;

public class GameProfile implements Serializable {
	
	public static final GameProfile EMPTY = new GameProfile("empty", Util.EMPTY_UUID);
	
	protected final String name;
	protected final UUID uuid;
	
	public GameProfile(String name, UUID uuid)  {
		this.name = name;
		this.uuid = uuid;
	}
	
	public GameProfile(CompoundTag tag) {
		this.name = tag.getString("name");
		this.uuid = TagUtil.readUUID(tag.getCompound("uuid"));
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("name", this.name);
		tag.putCompound("uuid", TagUtil.writeUUID(this.uuid));
		return tag;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("GameProfile{");
		builder.append("name=").append(this.name).append(",");
		builder.append("uuid=").append(this.uuid).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GameProfile gameProfile) {
			if (!this.name.equals(gameProfile.name)) {
				return false;
			} else {
				return this.uuid.equals(gameProfile.uuid);
			}
		}
		return false;
	}
	
}
