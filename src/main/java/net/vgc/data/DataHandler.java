package net.vgc.data;

import java.io.IOException;

public interface DataHandler {
	
	void load() throws IOException;
	
	void save() throws IOException;
	
}
