package net.luis.run;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.Constants;
import net.luis.account.AccountServer;
import net.luis.client.Client;
import net.luis.server.Server;
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
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		parser.accepts("client");
		parser.accepts("server");
		parser.accepts("account");
		OptionSpec<Boolean> debugMode = parser.accepts("debugMode").withRequiredArg().ofType(Boolean.class);
		parser.accepts("ide");
		OptionSet set = parser.parse(args);
		boolean client = set.has("client");
		boolean server = set.has("server");
		boolean account = set.has("account");
		Constants.IDE = set.has("ide");
		checkLaunch(client, server, account);
		if (client) {
			Constants.LAUNCH_TYPE = "client";
			Client.launch(Client.class, args);
		} else if (server) {
			Constants.LAUNCH_TYPE = "server";
			Server.launch(Server.class, args);
		} else if (account) {
			Constants.LAUNCH_TYPE = "account";
			AccountServer.launch(AccountServer.class, args);
		} else {
			LOGGER.error("A critical error occurred while launching the virtual game collection");
			System.exit(-1);
		}
	}
	
	private static void checkLaunch(boolean client, boolean server, boolean account) {
		if (client && server && account) {
			LOGGER.trace("Can not launch a client, a server and a account server");
			System.exit(-1);
		} else if (client && server) {
			LOGGER.trace("Can not launch a client and a server");
			System.exit(-1);
		} else if (client && account) {
			LOGGER.trace("Can not launch a client and a account server");
			System.exit(-1);
		} else if (server && account) {
			LOGGER.trace("Can not launch a server and a account server");
			System.exit(-1);
		} else if (!client && !server && !account) {
			LOGGER.trace("Unknown launch type for the virtual game collection, use '--client' to start a client, '--server' to start a server or '--account' to start a account server");
			System.exit(-1);
		}
	}
	
}
