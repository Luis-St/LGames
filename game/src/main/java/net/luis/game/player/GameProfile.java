package net.luis.game.player;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.TagUtils;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.ToString;
import net.luis.utils.util.Utils;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class GameProfile implements Encodable, Serializable {
	
	public static final GameProfile EMPTY = new GameProfile("empty", Utils.EMPTY_UUID);
	
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
		this.uuid = TagUtils.readUUID(tag.getCompound("UUID"));
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
		tag.putCompound("UUID", TagUtils.writeUUID(this.uuid));
		return tag;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GameProfile that)) return false;
		
		if (!this.name.equals(that.name)) return false;
		return this.uuid.equals(that.uuid);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.uuid);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
