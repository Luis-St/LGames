package net.luis.run;

import net.luis.game.application.InternalApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 *
 * @author Luis-st
 *
 */

public class Main {
	
	public static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		System.out.println();
		LOGGER.info("Init Logger");
		LOGGER.info("Run with Arguments: {}", Arrays.asList(args).toString().replace("[", "").replace("]", ""));
		InternalApplication.launch(InternalApplication.class, args);
	}
	
}
