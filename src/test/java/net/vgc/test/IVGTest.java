package net.vgc.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IVGTest {
	
	static final Logger LOGGER = LogManager.getLogger(IVGTest.class);
	
	void start() throws Exception;
	
	void stop() throws Exception;
	
}
