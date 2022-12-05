package net.vgc.data;

import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public interface DataHandler {
	
	void load() throws IOException;
	
	void save() throws IOException;
	
}
