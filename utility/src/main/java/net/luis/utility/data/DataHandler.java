package net.luis.utility.data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 * @author Luis-st
 *
 */

public interface DataHandler {
	
	void load(@NotNull String[] args) throws Exception;
	
	void save() throws IOException;
	
}
