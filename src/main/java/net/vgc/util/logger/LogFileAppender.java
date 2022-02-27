package net.vgc.util.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.google.common.collect.Lists;

public class LogFileAppender extends AbstractAppender {
	
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	protected static final String PATTERN = "yyyy-MM-dd-ii.log.gz";
	protected static final Path LOG_DIR = new File(System.getProperty("net.vgc.gameDir")).toPath().resolve("logs");
	protected static final Path LATEST_LOG = new File("latest.log").toPath();
	protected static final Path DEBUG_LOG = new File("debug.log").toPath();
	
	protected final String fileName;
	protected final int maxFileSize;
	
	protected Path log;
	protected FileWriter writer;
	
	public LogFileAppender(String name, Level minLevel, Level maxLevel, String fileName, int maxFileSize) {
		super(name, LevelRangeFilter.createFilter(minLevel, maxLevel, Result.ACCEPT, Result.DENY), PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%t/%level]: %msg%n%throwable").build(), false, Property.EMPTY_ARRAY);
		this.fileName = fileName;
		this.maxFileSize = maxFileSize;
	}
			
	public boolean isDebugLog() {
		return this.fileName.endsWith("debug.log");
	}
	
	@Override
	public void start() {
		super.start();
		try {
			Path path = LOG_DIR.resolve(this.isDebugLog() ? DEBUG_LOG : LATEST_LOG);
			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				this.file(path);
			} else {
				this.zip(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void file(Path path) throws IOException {
		Files.createFile(path);
		this.log = path;
	}
	
	protected String format(Path path) throws IOException {
		return PATTERN.replace("yyyy-MM-dd", DATE_FORMAT.format(new Date(System.currentTimeMillis()))).replace("ii", Integer.toString(this.fileCount(path)));
	}
	
	protected int fileCount(Path path) throws IOException {
		List<Integer> counts = Lists.newArrayList(path.toFile().listFiles()).stream().map(File::getName).filter((name) -> {
			return name.endsWith(".log.gz");
		}).map((name) -> {
			return Integer.valueOf(name.split("\\.")[0].split("-")[3]);
		}).collect(Collectors.toList());
		if (!counts.isEmpty()) {
			Collections.sort(counts);
			return counts.get(counts.size() - 1) + 1;
		}
		return 1;
	}
	
	protected void zip(Path path) throws IOException {
		String zipName = this.format(path);
        FileInputStream input = new FileInputStream(LOG_DIR.resolve(LATEST_LOG).toFile());
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(LOG_DIR.resolve(zipName).toFile()));
        output.putNextEntry(new ZipEntry(zipName.replace(".gz", ""))); 
        byte[] cache = new byte[2048];
        int count;
        while ((count = input.read(cache)) > 0) {
        	output.write(cache, 0, count);
        }
        output.close();
        input.close();
        Files.delete(path);
		this.file(path);
	}
	
	protected void createWriter() throws IOException {
		if (this.writer == null) {
			this.writer = new FileWriter(this.log.toFile(), true);
		}
	}
	
	protected boolean canAppend() {
		if (this.maxFileSize > 0) {
			long fileSize = this.log.toFile().length();
			return this.maxFileSize >= fileSize / 1024 / 1024;
		}
		return true;
	}
	
	@Override
	public void append(LogEvent event) {
		System.out.println("LogFileAppender.append()");
		String string = this.getLayout().toSerializable(event).toString();
		try {
			this.createWriter();
			if (Files.exists(this.log) && this.canAppend()) {
				this.writer.write(string);
				this.writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
