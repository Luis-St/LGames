package net.vgc.server.game.map;

import net.vgc.game.Game;
import net.vgc.game.map.AbstractGameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.server.Server;

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
