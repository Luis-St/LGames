package net.vgc.test;

import org.apache.logging.log4j.Logger;

import net.vgc.util.Util;

public interface IVGCest {
	
	static final Logger LOGGER = Util.getLogger(IVGCest.class);
	
	void start() throws Exception;
	
	void stop() throws Exception;
	
}
