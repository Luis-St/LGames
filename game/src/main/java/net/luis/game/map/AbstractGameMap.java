package net.luis.game.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameField;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameMap implements GameMap {
	
	private final Game game;
	private final List<GameField> fields;
	
	protected AbstractGameMap(Game game) {
		this.game = game;
		this.fields = Lists.newArrayList();
		this.addFields();
	}
	
	@Override
	public Game getGame() {
		return this.game;
	}
	
	@Override
	public List<GameField> getFields() {
		return ImmutableList.copyOf(this.fields);
	}
	
	protected void addField(GameField field) {
		this.fields.add(field);
	}
}
