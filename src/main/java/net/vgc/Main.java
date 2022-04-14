package net.vgc;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.account.AccountServer;
import net.vgc.client.Client;
import net.vgc.server.Server;
import net.vgc.util.Util;

public class Main {
	
	/* TODO's:
	 *  - add new info system form server -> client
	 *  - impl. singleplayer, multiplayer and settings (PlayerSettings)
	 *  - create Loading Steps, which are load from 0 til 1
	 *  - use ErrorWindow & interrupt loading (Loading Steps) while open/not choose
	 *  - add server side path for Network#getGameDirectory and Network#getResourceDirectory
	 *  - create value chooser similar to DirectoryChooser (for primitive values)
	 *  - layout and grafic rework
	 *  - language client side -> send Key through Network
	 *  - super class for Windows
	 *  - rework of AccountAgent -> add util methods & do no allow ' ' and ':' in PlayerAccount
	 */
	
	protected static final Logger LOGGER = LogManager.getLogger(Main.class);
	
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
		Util.warpStreams(set.has(debugMode) ? set.valueOf(debugMode) : false);
		Constans.IDE = set.has("ide");
		checkLaunch(client, server, account);
		if (client) {
			Constans.LAUNCH_TYPE = "client";
			Client.launch(Client.class, args);
		} else if (server) {
			Constans.LAUNCH_TYPE = "server";
			Server.launch(Server.class, args);
		} else if (account) {
			Constans.LAUNCH_TYPE = "account";
			AccountServer.launch(AccountServer.class, args);
		} else {
			System.exit(-1);
		}
	}
	
	protected static void checkLaunch(boolean client, boolean server, boolean account) {
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
			LOGGER.trace("Unknown launch type for the Virtual Game Collection, use '--client' to start a client, '--server' to start a server or '--account' to start a account server");
			System.exit(-1);
		}
	}

}
