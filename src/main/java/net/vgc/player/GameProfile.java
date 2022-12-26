package net.vgc.player;

import java.util.UUID;

import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.Equals;
import net.luis.utils.util.ToString;
import net.vgc.data.tag.TagUtil;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.Util;
import net.vgc.util.annotation.DecodingConstructor;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class GameProfile implements Encodable, Serializable {
	
	public static final GameProfile EMPTY = new GameProfile("empty", Util.EMPTY_UUID);
	
	private final String name;
	private final UUID uuid;
	
	public GameProfile(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	@DecodingConstructor
	private GameProfile(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	public GameProfile(CompoundTag tag) {
		this.name = tag.getString("Name");
		this.uuid = TagUtil.readUUID(tag.getCompound("UUID"));
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.getName());
		buffer.writeUUID(this.getUUID());
	}
	
	@Override
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Name", this.name);
		tag.putCompound("UUID", TagUtil.writeUUID(this.uuid));
		return tag;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
}
