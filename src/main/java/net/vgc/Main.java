package net.vgc;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.client.Client;
import net.vgc.server.Server;
import net.vgc.server.account.AccountServer;
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
		parser.accepts("accountServer");
		OptionSpec<Boolean> debugMode = parser.accepts("debugMode").withRequiredArg().ofType(Boolean.class);
		parser.accepts("ide");
		OptionSet set = parser.parse(args);
		boolean client = set.has("client");
		boolean server = set.has("server");
		boolean accountServer = set.has("accountServer");
		Util.warpStreams(set.has(debugMode) ? set.valueOf(debugMode) : false);
		Constans.IDE = set.has("ide");
		if (client && server) {
			LOGGER.trace("Can not launch a client and a server");
			System.exit(-1);
		} else if (client) {
			Constans.LAUNCH_TYPE = "client";
			Client.launch(Client.class, args);
		} else if (server) {
			Constans.LAUNCH_TYPE = "server";
			Server.launch(Server.class, args);
		} else if (accountServer) {
			Constans.LAUNCH_TYPE = "account_server";
			AccountServer.launch(AccountServer.class, args);
		} else {
			LOGGER.trace("Unknown launch type for the Virtual Game Collection, use '--client' to start a client, '--server' to start a server or '--accountServer' to start a account server");
			System.exit(-1);
		}
	}

}
