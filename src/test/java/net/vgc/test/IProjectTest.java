package net.project.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IProjectTest {
	
	static final Logger LOGGER = LogManager.getLogger(IProjectTest.class);
	
	void start() throws Exception;
	
	void stop() throws Exception;
	
}
