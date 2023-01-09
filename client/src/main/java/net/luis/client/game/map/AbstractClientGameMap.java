package net.luis.client.game.map;

import net.luis.client.Client;
import net.luis.game.Game;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.GameFigure;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractClientGameMap extends AbstractGameMap {
	
	private final Client client;
	
	protected AbstractClientGameMap(Client client, Game game) {
		super(game);
		this.client = client;
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		this.getFields().forEach(GameField::clear);
	}
	
	public Client getClient() {
		return this.client;
	}
	
	@Override
	public final boolean moveFigureTo(GameFigure figure, GameField field) {
		LOGGER.warn("Can not move figure {} of player {} on client", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
}
