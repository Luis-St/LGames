package net.project.test;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.project.util.ClasspathInspector;
import net.project.util.ReflectionUtil;

public class TestMain {
	
	protected static final Logger LOGGER = LogManager.getLogger(TestMain.class);
	public static Path resourceDir;
	
	public static void main(String[] args) throws Exception {
		System.out.println();
		LOGGER.info("Init Logger");
		LOGGER.info("Main run with Aruments: {}", Arrays.asList(args).toString().replace("[", "").replace("]", ""));
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSet set = parser.parse(args);
		if (!set.has(resourceDir)) {
			throw new IllegalStateException("Fail to get resource directory from program arguments");
		} else {
			TestMain.resourceDir = set.valueOf(resourceDir).toPath();
		}
		startTests();
	}
	
	protected static void startTests() {
		List<Class<?>> classes = ClasspathInspector.getTestClasses();
		long mainTime = System.currentTimeMillis();
		LOGGER.info("Start testing");
		LOGGER.info("");
		for (Class<?> clazz : classes) {
			try {
				TestMain.class.getClassLoader().loadClass(clazz.getName());
				if (clazz.isAnnotationPresent(ProjectTest.class) && ReflectionUtil.hasInterface(clazz, IProjectTest.class)) {
					ProjectTest projectTest = clazz.getAnnotation(ProjectTest.class);
					if (projectTest.shoudLoad()) {
						long testTime = System.currentTimeMillis();
						IProjectTest test = (IProjectTest) ReflectionUtil.newInstance(clazz);
						LOGGER.info("Start testing of {}", clazz.getSimpleName());
						test.start();
						LOGGER.info("Stop testing of {}", clazz.getSimpleName());
						test.stop();
						LOGGER.info("Testing of {} took {} ms", clazz.getSimpleName(), System.currentTimeMillis() - testTime);
						LOGGER.info("");
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error while testing ", e);
			}
		}
		LOGGER.info("Testing took {} ms", System.currentTimeMillis() - mainTime);
	}

}
