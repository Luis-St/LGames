package net.vgc.test.data.tag.tags;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.StringTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCTestMain;
import net.vgc.test.VGCTest;

@VGCTest
public class StringTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/string_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		StringTag tag = StringTag.valueOf("StringTagTest");
		Tag.write(VGCTestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCTestMain.resourceDir.resolve(this.path));
		LOGGER.debug("{}", tag);
	}

}
