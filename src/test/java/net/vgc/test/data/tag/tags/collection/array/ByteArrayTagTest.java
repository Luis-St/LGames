package net.vgc.test.data.tag.tags.collection.array;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.collection.array.ByteArrayTag;
import net.vgc.data.tag.tags.numeric.ByteTag;
import net.vgc.test.IVGTest;
import net.vgc.test.VGCTest;
import net.vgc.test.VGCTestMain;

@VGCTest
public class ByteArrayTagTest implements IVGTest {
	
	protected final Path path = new File("test/tag/collection/array/byte_array_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		ByteArrayTag tag = new ByteArrayTag();
		for (int i = 0; i < 4; i++) {
			tag.add(ByteTag.valueOf((byte) i));
		}
		Tag.write(VGCTestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCTestMain.resourceDir.resolve(this.path));
		if (tag instanceof ByteArrayTag arrayTag) {
			for (int i = 0; i < arrayTag.size(); i++) {
				LOGGER.debug("{}:{}", i, arrayTag.get(i));
			}
		}
	}

}
