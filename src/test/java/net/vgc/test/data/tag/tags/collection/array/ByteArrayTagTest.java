package net.project.test.data.tag.tags.collection.array;

import java.io.File;
import java.nio.file.Path;

import net.project.data.tag.Tag;
import net.project.data.tag.tags.collection.array.ByteArrayTag;
import net.project.data.tag.tags.numeric.ByteTag;
import net.project.test.IProjectTest;
import net.project.test.ProjectTest;
import net.project.test.TestMain;

@ProjectTest
public class ByteArrayTagTest implements IProjectTest {
	
	protected final Path path = new File("test/tag/collection/array/byte_array_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		ByteArrayTag tag = new ByteArrayTag();
		for (int i = 0; i < 4; i++) {
			tag.add(ByteTag.valueOf((byte) i));
		}
		Tag.write(TestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(TestMain.resourceDir.resolve(this.path));
		if (tag instanceof ByteArrayTag arrayTag) {
			for (int i = 0; i < arrayTag.size(); i++) {
				LOGGER.debug("{}:{}", i, arrayTag.get(i));
			}
		}
	}

}
