package net.project.test.data.tag.tags.numeric;

import java.io.File;
import java.nio.file.Path;

import net.project.data.tag.Tag;
import net.project.data.tag.tags.numeric.DoubleTag;
import net.project.test.IProjectTest;
import net.project.test.ProjectTest;
import net.project.test.TestMain;

@ProjectTest
public class DoubleTagTest implements IProjectTest {
	
	protected final Path path = new File("test/tag/numeric/double_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		DoubleTag tag = DoubleTag.valueOf((double) 0.0424500450785454);
		Tag.write(TestMain.resourceDir.resolve(this.path), tag);
		LOGGER.debug("{}", tag);
	}

	@Override
	public void stop() throws Exception {
		Tag tag = Tag.load(TestMain.resourceDir.resolve(this.path));
		LOGGER.debug("{}", tag);
	}

}
