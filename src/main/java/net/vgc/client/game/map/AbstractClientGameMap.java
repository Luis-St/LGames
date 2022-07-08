package net.vgc.client.game.map;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.game.Game;
import net.vgc.game.map.AbstractGameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;

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
