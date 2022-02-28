package net.vgc.test.data.tag.tags;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.test.IVGTest;
import net.vgc.test.VGCTest;
import net.vgc.test.VGCTestMain;

@VGCTest
public class CompoundTagTest implements IVGTest {
	
	protected final Path path = new File("test/tag/compound_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		CompoundTag tag = new CompoundTag();
		tag.putInt("test_int", 10);
		tag.putLong("test_long", 487845);
		tag.putString("test_string", "string");
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putInt("test_int", 4545);
		tag.put("test_compound", compoundTag );
		tag.putBoolean("test_boolean", false);
		Tag.write(VGCTestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(VGCTestMain.resourceDir.resolve(this.path));
		if (tag instanceof CompoundTag compoundTag) {
			LOGGER.debug("{}:{}", "test_int", compoundTag.getInt("test_int"));
			LOGGER.debug("{}:{}", "test_long", compoundTag.getLong("test_long"));
			LOGGER.debug("{}:{}", "test_string", compoundTag.getString("test_string"));
			LOGGER.debug("{}:{}", "test_compound", compoundTag.getCompound("test_compound"));
			LOGGER.debug("{}:{}", "test_boolean", compoundTag.getBoolean("test_boolean"));
		}
		
	}

}
