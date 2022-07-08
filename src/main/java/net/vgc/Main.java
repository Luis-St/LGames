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
	 *  - add GameMap#canMove -> fix Ludo move figure issues include GamePlayer#canMoveFigure (used for action skip)
	 *  - sum #setOnAction of LudoClientMap & fix issue -> not ignore next field
	 *  - check game casts via instance of
	 *  - use WinHandler#canPlayerWin and rework of hasPlayerFinished -> in Ludo, TTT and Wins4 game
	 *  - rework of FieldRenderState and shadow rendering
	 *  - LobbyScreen does not update players correctly
	 *  - new registry for Packets
	 *  - add interfaces MoveableGame, MoveableGameMap, MoveableGamePlayer, MoveableFigure (ActionGame) -> add Abstract impl, use common values, add all methods to it bas interface, remove server and client sub interfaces
	 *  - add new Test system which skip the loading suff
	 *  - rework of settings system, create screen dynamic -> create Screen by Setting Type + fix issue (List and Field are not synced -> different instances) -> remove auto save of settings
	 *  - add server settings
	 *  - impl. remove player correctly, remove figures, ...
	 *  - add super class for Windows
	 *  - new version system
	 *  - score rework & add way to display score in server window
	 *  - create Loading Steps, which are load from 0% til 100% -> use ErrorWindow & interrupt loading (Loading Steps) while open/not choose
	 *  - add new info system form server -> client (add InfoInputPane)
	 *  - rework of layout and grafic (use always InputPane)
	 *  - account server close -> packet to client which skip loggout (without sync to account server)
	 *  - fix Logger (testing)
	 *  
	 *  https://codetabs.com/count-loc/count-loc-online.html -> Luis-st/School-Project & game-api
	 */
	
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
