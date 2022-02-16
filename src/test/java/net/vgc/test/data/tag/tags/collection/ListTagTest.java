package net.vgc.test.data.tag.tags.collection;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.collection.ListTag;
import net.vgc.data.tag.tags.numeric.ByteTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCMain;
import net.vgc.test.VGCTest;

@VGCTest
public class ListTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/collection/list_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		ListTag tag = new ListTag();
		for (int i = 10; i < 21; i++) {
			tag.add(ByteTag.valueOf((byte) i));
		}
		Tag.write(VGCMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCMain.resourceDir.resolve(this.path));
		if (tag instanceof ListTag listTag) {
			for (int i = 0; i < listTag.size(); i++) {
				LOGGER.debug("{}:{}", i, listTag.get(i));
			}
		}
	}

}
