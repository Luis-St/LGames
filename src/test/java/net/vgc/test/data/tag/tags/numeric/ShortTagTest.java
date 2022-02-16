package net.vgc.test.data.tag.tags.numeric;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.numeric.ShortTag;
import net.vgc.test.IVGCest;
import net.vgc.test.VGCMain;
import net.vgc.test.VGCTest;

@VGCTest
public class ShortTagTest implements IVGCest {
	
	protected final Path path = new File("test/tag/numeric/short_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		ShortTag tag = ShortTag.valueOf((short) 786);
		Tag.write(VGCMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCMain.resourceDir.resolve(this.path));
		LOGGER.debug("{}", tag);
	}

}
