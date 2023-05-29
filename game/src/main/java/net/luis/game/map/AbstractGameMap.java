package net.luis.game.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.field.GameField;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameMap implements GameMap {
	
	private final Game game;
	private final List<GameField> fields;
	
	protected AbstractGameMap(Game game) {
		this.game = Objects.requireNonNull(game, "Game must not be null");
		this.fields = Lists.newArrayList();
		this.init();
		this.addFields();
	}
	
	@Override
	public @NotNull Game getGame() {
		return this.game;
	}
	
	@Override
	public @NotNull List<GameField> getFields() {
		return ImmutableList.copyOf(this.fields);
	}
	
	protected void addField(GameField field) {
		this.fields.add(Objects.requireNonNull(field, "Field must not be null"));
	}
}
