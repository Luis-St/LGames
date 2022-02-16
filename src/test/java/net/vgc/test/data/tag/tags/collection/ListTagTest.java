package net.project.test.data.tag.tags.collection;

import java.io.File;
import java.nio.file.Path;

import net.project.data.tag.Tag;
import net.project.data.tag.tags.collection.ListTag;
import net.project.data.tag.tags.numeric.ByteTag;
import net.project.test.IProjectTest;
import net.project.test.ProjectTest;
import net.project.test.TestMain;

@ProjectTest
public class ListTagTest implements IProjectTest {
	
	protected final Path path = new File("test/tag/collection/list_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		ListTag tag = new ListTag();
		for (int i = 10; i < 21; i++) {
			tag.add(ByteTag.valueOf((byte) i));
		}
		Tag.write(TestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(TestMain.resourceDir.resolve(this.path));
		if (tag instanceof ListTag listTag) {
			for (int i = 0; i < listTag.size(); i++) {
				LOGGER.debug("{}:{}", i, listTag.get(i));
			}
		}
	}

}
