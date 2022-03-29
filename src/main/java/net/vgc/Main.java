package net.vgc;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.client.Client;
import net.vgc.server.Server;
import net.vgc.util.Util;

public class Main {
	
	protected static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		System.out.println();
		LOGGER.info("Init Logger");
		LOGGER.info("Run with Arguments: {}", Arrays.asList(args).toString().replace("[", "").replace("]", ""));
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		parser.accepts("client");
		parser.accepts("server");
		OptionSpec<Boolean> debugMode = parser.accepts("debugMode").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		Util.warpStreams(set.has(debugMode) ? set.valueOf(debugMode) : false);
		boolean client = set.has("client");
		boolean server = set.has("server");
		if (client && server) {
			LOGGER.trace("Can not launch a client and a server");
			System.exit(-1);
		} else if (client) {
			System.setProperty("net.vgc.launch.type", "client");
			Client.launch(Client.class, args);
		} else if (server) {
			System.setProperty("net.vgc.launch.type", "server");
			Server.launch(Server.class, args);
		} else {
			LOGGER.trace("Unknown launch type for the Virtual Game Collection, use '--server' to start a server and '--client' to start a client");
			System.exit(-1);
		}
	}

}
