package net.vgc.server.game.games.wins4.map;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.server.game.games.wins4.Wins4ServerGame;
import net.vgc.server.game.games.wins4.map.field.Wins4ServerField;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class Wins4ServerMap implements ServerGameMap {
	
	protected final Wins4ServerGame game;
	protected final List<Wins4ServerField> fields = Util.make(Lists.newArrayList(), (list) -> {
		for (int i = 0; i < 42; i++) {
			list.add(new Wins4ServerField(Wins4FieldPos.of(i)));
		}
	});
	
	public Wins4ServerMap(Wins4ServerGame game) {
		this.game = game;
	}
	
	@Override
	public void init(List<? extends GamePlayer> players) {
		this.fields.forEach(Wins4ServerField::clear);
	}

	@Override
	public Wins4ServerGame getGame() {
		return this.game;
	}

	@Override
	public List<Wins4ServerField> getFields() {
		return this.fields;
	}

	@Nullable
	@Override
	public Wins4ServerField getField(GameFigure figure) {
		for (Wins4ServerField field : this.getFields()) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public Wins4ServerField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		return this.fields.get(fieldPos.getPosition());
	}

	@Nullable
	@Override
	public Wins4ServerField getNextField(GameFigure figure, int count) {
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the 4 wins figures does not have a next field", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return null;
	}
	
	public List<Wins4ServerField> getFieldsForColumn(int column) {
		if (Mth.isInBounds(column, 0, 6)) {
			List<Wins4ServerField> fields = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				fields.add(this.getField(null, null, Wins4FieldPos.of(i, column)));
			}
			return fields;
		}
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ServerField> getHomeFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ServerField> getStartFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public List<Wins4ServerField> getWinFields(GamePlayerType playerType) {
		return Lists.newArrayList();
	}

	@Override
	public boolean moveFigureTo(GameFigure figure, GameField field) {
		LOGGER.warn("Fail to move figure {} of player {}, since the 4 wins figures are not moveable", figure.getCount(), figure.getPlayer().getPlayer().getProfile().getName());
		return false;
	}
	
	@Override
	public String toString() {
		return "Win4ServerMap";
	}
	
}
