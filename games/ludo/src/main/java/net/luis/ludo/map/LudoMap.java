package net.luis.ludo.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.ludo.map.field.LudoFieldPos;
import net.luis.ludo.map.field.LudoFieldType;
import net.luis.ludo.player.LudoPlayerType;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.utils.exception.InvalidValueException;
import net.luis.utils.math.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getGame#getMap")
public class LudoMap extends AbstractGameMap {
	
	private final List<GameField> homeFields = Lists.newArrayList();
	private final List<GameField> winFields = Lists.newArrayList();
	
	public LudoMap(Game game) {
		super(game);
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		for (GamePlayer gamePlayer : players) {
			LOGGER.debug("Add figures ({}) of player {}, to their home fields", gamePlayer.getFigures().size(), gamePlayer.getPlayer().getProfile().getName());
			for (GameFigure figure : gamePlayer.getFigures()) {
				Objects.requireNonNull(this.getField(LudoFieldType.HOME, gamePlayer.getPlayerType(), figure.getHomePos())).setFigure(figure);
			}
		}
	}
	
	@Override
	public void addFields() {
	
	}
	
	private void addField(LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos, int column, int row) {
		
	}
	
	private LudoPlayerType getFieldColor(int i) {
		if (Mth.isInBounds(i, 0, 3)) {
			return LudoPlayerType.GREEN;
		} else if (Mth.isInBounds(i, 4, 7)) {
			return LudoPlayerType.YELLOW;
		} else if (Mth.isInBounds(i, 8, 11)) {
			return LudoPlayerType.BLUE;
		} else if (Mth.isInBounds(i, 12, 15)) {
			return LudoPlayerType.RED;
		}
		throw new InvalidValueException("Fail to get field color for index " + i);
	}
	
	@Override
	public List<GameField> getFields() {
		return Stream.of(super.getFields(), this.homeFields, this.winFields).flatMap(List::stream).collect(ImmutableList.toImmutableList());
	}
	
	@Override
	public @Nullable GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, GameFieldPos fieldPos) {
		return null;
	}
	
	@Override
	public @Nullable GameField getNextField(GameFigure figure, int count) {
		return null;
	}
	
	private List<GameField> getFieldsFrom(GamePlayerType playerType, List<GameField> fields) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN -> fields.subList(0, 4);
			case YELLOW -> fields.subList(4, 8);
			case BLUE -> fields.subList(8, 12);
			case RED -> fields.subList(12, 16);
			default -> {
				LOGGER.warn("Fail to get home fields for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		return this.getFieldsFrom(playerType, this.homeFields);
	}
	
	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN, YELLOW, BLUE, RED -> Lists.newArrayList(this.getFields().get(Objects.requireNonNull(LudoFieldPos.of(playerType, 0)).getPosition()));
			default -> {
				LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		return this.getFieldsFrom(playerType, this.winFields);
	}
	
}
