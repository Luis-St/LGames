package net.project.data.tag;

import net.project.data.tag.tags.CompoundTag;
import net.project.data.tag.tags.EndTag;
import net.project.data.tag.tags.StringTag;
import net.project.data.tag.tags.collection.ListTag;
import net.project.data.tag.tags.collection.array.ByteArrayTag;
import net.project.data.tag.tags.collection.array.IntArrayTag;
import net.project.data.tag.tags.collection.array.LongArrayTag;
import net.project.data.tag.tags.numeric.ByteTag;
import net.project.data.tag.tags.numeric.DoubleTag;
import net.project.data.tag.tags.numeric.FloatTag;
import net.project.data.tag.tags.numeric.IntTag;
import net.project.data.tag.tags.numeric.LongTag;
import net.project.data.tag.tags.numeric.ShortTag;

public class TagTypes {
	
	public static final TagType<?>[] TYPES = new TagType<?>[] { 
		EndTag.TYPE, ByteTag.TYPE, ShortTag.TYPE, IntTag.TYPE,LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, 
		StringTag.TYPE, ByteArrayTag.TYPE, IntArrayTag.TYPE,LongArrayTag.TYPE, ListTag.TYPE, CompoundTag.TYPE 
	};
	
	public static TagType<?> getType(int id) {
		return id >= 0 && id < TYPES.length ? TYPES[id] : TagType.createInvalid(id);
	}
	
}
