package net.project.test.data.tag.tags;

import java.io.File;
import java.nio.file.Path;

import net.project.data.tag.Tag;
import net.project.data.tag.tags.StringTag;
import net.project.test.IProjectTest;
import net.project.test.ProjectTest;
import net.project.test.TestMain;

@ProjectTest
public class StringTagTest implements IProjectTest {
	
	protected final Path path = new File("test/tag/string_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		StringTag tag = StringTag.valueOf("StringTagTest");
		Tag.write(TestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(TestMain.resourceDir.resolve(this.path));
		LOGGER.debug("{}", tag);
	}

}
