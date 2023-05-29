package net.luis.game.player;

import net.luis.netcore.buffer.FriendlyByteBuffer;
import net.luis.netcore.buffer.decode.Decodable;
import net.luis.netcore.buffer.encode.Encodable;
import net.luis.utils.data.serialization.Deserializable;
import net.luis.utils.data.serialization.Serializable;
import net.luis.utils.data.tag.TagUtils;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@Deserializable
public class GameProfile implements Serializable, Encodable, Decodable {
	
	public static final GameProfile EMPTY = new GameProfile("empty", Utils.EMPTY_UUID);
	
	private final String name;
	private final UUID uniqueId;
	
	public GameProfile(String name, UUID uniqueId) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.uniqueId = Objects.requireNonNull(uniqueId, "Unique id must not be null");
	}
	
	//region IO
	public GameProfile(@NotNull FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.uniqueId = buffer.readUniqueId();
	}
	
	public GameProfile(@NotNull CompoundTag tag) {
		this.name = tag.getString("Name");
		this.uniqueId = TagUtils.readUUID(tag.getCompound("UniqueId"));
	}
	//endregion
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull UUID getUniqueId() {
		return this.uniqueId;
	}
	
	//region IO
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeString(this.getName());
		buffer.writeUniqueId(this.getUniqueId());
	}
	
	@Override
	public @NotNull CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Name", this.name);
		tag.putCompound("UniqueId", TagUtils.writeUUID(this.uniqueId));
		return tag;
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GameProfile that)) return false;
		
		if (!this.name.equals(that.name)) return false;
		return this.uniqueId.equals(that.uniqueId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.uniqueId);
	}
	
	@Override
	public String toString() {
		return "GameProfile{name='" + this.name + '\'' + ", uuid=" + this.uniqueId + "}";
	}
	//endregion
}
