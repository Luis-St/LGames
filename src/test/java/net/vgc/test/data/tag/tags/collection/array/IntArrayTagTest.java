package net.vgc.test.data.tag.tags.collection.array;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.collection.array.IntArrayTag;
import net.vgc.data.tag.tags.numeric.IntTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCTest;
import net.vgc.test.VGCTestMain;

@VGCTest
public class IntArrayTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/collection/array/int_array_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		IntArrayTag tag = new IntArrayTag();
		for (int i = 0; i < 4; i++) {
			tag.add(IntTag.valueOf((int) i));
		}
		Tag.write(VGCTestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCTestMain.resourceDir.resolve(this.path));
		if (tag instanceof IntArrayTag arrayTag) {
			for (int i = 0; i < arrayTag.size(); i++) {
				LOGGER.debug("{}:{}", i, arrayTag.get(i));
			}
		}
	}

}

