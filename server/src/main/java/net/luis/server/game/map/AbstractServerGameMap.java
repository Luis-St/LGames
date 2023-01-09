package net.luis.server.game.map;

import net.luis.server.Server;
import net.luis.game.Game;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractServerGameMap extends AbstractGameMap {
	
	private final Server server;
	
	protected AbstractServerGameMap(Server server, Game game) {
		super(game);
		this.server = server;
	}
	
	@Override
	public final void init() {
		LOGGER.warn("Can not initialize the client map settings on server");
	}
	
	public Server getServer() {
		return this.server;
	}
	
	@Override
	public final GameField getSelectedField() {
		LOGGER.warn("Can not get the selected field on server");
		return null;
	}
	
}
