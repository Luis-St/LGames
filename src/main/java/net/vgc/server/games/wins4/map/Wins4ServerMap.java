package net.vgc.server.games.wins4.map;

import com.google.common.collect.Lists;
import net.luis.utils.math.Mth;
import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.wins4.map.field.Wins4FieldPos;
import net.vgc.server.Server;
import net.vgc.server.game.map.AbstractServerGameMap;
import net.vgc.server.games.wins4.map.field.Wins4ServerField;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerMap extends AbstractServerGameMap {
	
	public Wins4ServerMap(Server server, Game game) {
		super(server, game);
		this.addFields();
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		this.getFields().forEach(GameField::clear);
	}
	
	@Override
	public void addFields() {
		for (int i = 0; i < 42; i++) {
			this.addField(new Wins4ServerField(this, Wins4FieldPos.of(i)));
		}
	}
	
	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}
	
	@Nullable
	@Override
	public final GameField getNextField(GameFigure figure, int count) {
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the 4 wins figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	public List<GameField> getFieldsForColumn(int column) {
		if (Mth.isInBounds(column, 0, 6)) {
			List<GameField> fields = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				fields.add(this.getField(null, null, Wins4FieldPos.of(i, column)));
			}
			return fields;
		}
		return Lists.newArrayList();
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
		LOGGER.warn("Fail to move figure {} of player {}, since the 4 wins figures are not moveable", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
	@Override
	public String toString() {
		return "Win4ServerMap";
	}
	
}
