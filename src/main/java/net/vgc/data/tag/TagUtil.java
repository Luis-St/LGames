package net.vgc.data.tag;

import java.util.UUID;

import net.vgc.data.tag.tags.CompoundTag;

public class TagUtil {
	
	public static CompoundTag writeUUID(UUID uuid) {
		CompoundTag tag = new CompoundTag();
		tag.putLong("mostBits", uuid.getMostSignificantBits());
		tag.putLong("leastBits", uuid.getLeastSignificantBits());
		return tag;
	}
	
	public static UUID readUUID(CompoundTag tag) {
		return new UUID(tag.getLong("mostBits"), tag.getLong("leastBits"));
	}
	
}
