package net.luis.util.streams;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author Luis-st
 *
 */

public class InfoPrintStream extends PrintStream {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final String name;
	
	public InfoPrintStream(String name, OutputStream stream) {
		super(stream);
		this.name = name;
	}
	
	@Override
	public void println(String string) {
		this.log(string);
	}
	
	@Override
	public void println(Object object) {
		this.log(String.valueOf(object));
	}
	
	protected void log(String string) {
		LOGGER.info("[{}] {}", this.name, string);
	}
	
}
