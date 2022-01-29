package net.project.test.data.tag.tags.collection.array;

import java.io.File;
import java.nio.file.Path;

import net.project.data.tag.Tag;
import net.project.data.tag.tags.collection.array.LongArrayTag;
import net.project.data.tag.tags.numeric.LongTag;
import net.project.test.IProjectTest;
import net.project.test.ProjectTest;
import net.project.test.TestMain;

@ProjectTest
public class LongArrayTagTest implements IProjectTest {
	
	protected final Path path = new File("test/tag/collection/array/long_array_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		LongArrayTag tag = new LongArrayTag();
		for (int i = 0; i < 4; i++) {
			tag.add(LongTag.valueOf((long) i));
		}
		Tag.write(TestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(TestMain.resourceDir.resolve(this.path));
		if (tag instanceof LongArrayTag arrayTag) {
			for (int i = 0; i < arrayTag.size(); i++) {
				LOGGER.debug("{}:{}", i, arrayTag.get(i));
			}
		}
	}

}

