package net.vgc.server.game.map;

import net.vgc.game.Game;
import net.vgc.game.map.AbstractGameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.server.dedicated.DedicatedServer;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractServerGameMap extends AbstractGameMap {
	
	private final DedicatedServer server;
	
	protected AbstractServerGameMap(DedicatedServer server, Game game) {
		super(game);
		this.server = server;
	}
	
	@Override
	public final void init() {
		LOGGER.warn("Can not initialize the client map settings on server");
	}
	
	public DedicatedServer getServer() {
		return this.server;
	}
	
	@Override
	public final GameField getSelectedField() {
		LOGGER.warn("Can not get the selected field on server");
		return null;
	}
	
}
