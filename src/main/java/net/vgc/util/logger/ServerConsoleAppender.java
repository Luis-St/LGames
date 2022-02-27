package net.vgc.util.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class ServerConsoleAppender extends AbstractAppender {
	
	protected static final ServerConsoleAppender INSTANCE = new ServerConsoleAppender("ServerConsoleAppender");
	
	protected final BlockingQueue<String> queue;
	
	public ServerConsoleAppender(String name) {
		super(name, LevelRangeFilter.createFilter(Level.INFO, Level.FATAL, Result.ACCEPT, Result.DENY), PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%t/%level]: %msg%n%throwable").build(), false, Property.EMPTY_ARRAY);
		this.queue = new LinkedBlockingDeque<>();
	}
	
	public static ServerConsoleAppender getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void append(LogEvent event) {
		System.out.println("ServerConsoleAppender.append()");
		if (this.queue.size() >= 250) {
			this.queue.clear();
		}
		this.queue.add(this.getLayout().toSerializable(event).toString());	
	}
	
	public String getNextLog() {
		try {
			return this.queue.take();
		} catch (InterruptedException e) {
			
		}
		return null;
	}

}
