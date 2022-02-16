package net.vgc.test.data.tag.tags.collection.array;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.collection.array.LongArrayTag;
import net.vgc.data.tag.tags.numeric.LongTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCTestMain;
import net.vgc.test.VGCTest;

@VGCTest
public class LongArrayTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/collection/array/long_array_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		LongArrayTag tag = new LongArrayTag();
		for (int i = 0; i < 4; i++) {
			tag.add(LongTag.valueOf((long) i));
		}
		Tag.write(VGCTestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCTestMain.resourceDir.resolve(this.path));
		if (tag instanceof LongArrayTag arrayTag) {
			for (int i = 0; i < arrayTag.size(); i++) {
				LOGGER.debug("{}:{}", i, arrayTag.get(i));
			}
		}
	}

}

