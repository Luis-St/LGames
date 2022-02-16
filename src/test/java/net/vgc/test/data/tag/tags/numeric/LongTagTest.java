package net.vgc.test.data.tag.tags.numeric;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.numeric.LongTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCMain;
import net.vgc.test.VGCTest;

@VGCTest
public class LongTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/numeric/long_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		LongTag tag = LongTag.valueOf((long) 478456877848572187L);
		Tag.write(VGCMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCMain.resourceDir.resolve(this.path));
		LOGGER.debug("{}", tag);
	}

}
