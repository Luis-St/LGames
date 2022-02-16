package net.vgc.data.tag.visitor;

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

public interface TagVisitor {
	
	void visitByte(ByteTag tag);

	void visitShort(ShortTag tag);

	void visitInt(IntTag tag);

	void visitLong(LongTag tag);

	void visitFloat(FloatTag tag);

	void visitDouble(DoubleTag tag);
	
	void visitString(StringTag tag);
	
	void visitByteArray(ByteArrayTag tag);

	void visitIntArray(IntArrayTag tag);

	void visitLongArray(LongArrayTag tag);

	void visitList(ListTag tag);

	void visitCompound(CompoundTag tag);
	
	void visitEnd(EndTag tag);
	
}
