package net.vgc.data.tag;

import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.data.tag.tags.EndTag;
import net.vgc.data.tag.tags.StringTag;
import net.vgc.data.tag.tags.collection.ListTag;
import net.vgc.data.tag.tags.collection.array.ByteArrayTag;
import net.vgc.data.tag.tags.collection.array.IntArrayTag;
import net.vgc.data.tag.tags.collection.array.LongArrayTag;
import net.vgc.data.tag.tags.numeric.ByteTag;
import net.vgc.data.tag.tags.numeric.DoubleTag;
import net.vgc.data.tag.tags.numeric.FloatTag;
import net.vgc.data.tag.tags.numeric.IntTag;
import net.vgc.data.tag.tags.numeric.LongTag;
import net.vgc.data.tag.tags.numeric.ShortTag;

public class TagTypes {
	
	public static final TagType<?>[] TYPES = new TagType<?>[] { 
		EndTag.TYPE, ByteTag.TYPE, ShortTag.TYPE, IntTag.TYPE,LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, 
		StringTag.TYPE, ByteArrayTag.TYPE, IntArrayTag.TYPE,LongArrayTag.TYPE, ListTag.TYPE, CompoundTag.TYPE 
	};
	
	public static TagType<?> getType(int id) {
		return id >= 0 && id < TYPES.length ? TYPES[id] : TagType.createInvalid(id);
	}
	
}
