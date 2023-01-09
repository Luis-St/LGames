package net.luis.server.games.ttt.map;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.ttt.map.field.TTTFieldPos;
import net.luis.server.Server;
import net.luis.server.game.map.AbstractServerGameMap;
import net.luis.server.games.ttt.map.field.TTTServerField;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class TTTServerMap extends AbstractServerGameMap {
	
	public TTTServerMap(Server server, Game game) {
		super(server, game);
		this.addFields();
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		this.getFields().forEach(GameField::clear);
	}
	
	@Override
	public void addFields() {
		for (int i = 0; i < 9; i++) {
			this.addField(new TTTServerField(this, TTTFieldPos.of(i)));
		}
	}
	
	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}
	
	@Nullable
	@Override
	public final GameField getNextField(GameFigure figure, int count) {
		GameMap.LOGGER.warn("Fail to get next field for figure {} of player {}, since tic tac toe figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	@Override
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public final boolean moveFigureTo(GameFigure figure, GameField field) {
		GameMap.LOGGER.warn("Fail to move figure {} of player {}, since tic tac toe figures are not movable", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
	@Override
	public String toString() {
		return "TTTServerMap";
	}
	
}
