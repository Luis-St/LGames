package net.luis.server.games.ludo.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.luis.common.exception.InvalidValueException;
import net.luis.game.Game;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.ludo.map.field.LudoFieldPos;
import net.luis.games.ludo.map.field.LudoFieldType;
import net.luis.games.ludo.player.LudoPlayerType;
import net.luis.server.Server;
import net.luis.server.game.map.AbstractServerGameMap;
import net.luis.server.games.ludo.map.field.LudoServerField;
import net.luis.server.games.ludo.player.LudoServerPlayer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerMap extends AbstractServerGameMap {
	
	private final List<GameField> homeFields;
	private final List<GameField> winFields;
	
	public LudoServerMap(Server server, Game game) {
		super(server, game);
		this.homeFields = Lists.newArrayList();
		this.winFields = Lists.newArrayList();
		this.addFields();
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		super.init(players);
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer instanceof LudoServerPlayer player) {
				GameMap.LOGGER.debug("Add figures ({}) of player {}, to their home fields", player.getFigures().size(), player.getPlayer().getProfile().getName());
				for (GameFigure figure : player.getFigures()) {
					Objects.requireNonNull(this.getField(LudoFieldType.HOME, player.getPlayerType(), figure.getHomePos())).setFigure(figure);
				}
			} else {
				GameMap.LOGGER.error("Can not add a game player of type {} to a ludo game", gamePlayer.getClass().getSimpleName());
				throw new RuntimeException("Can not add a game player of type " + gamePlayer.getClass().getSimpleName() + " to a ludo game");
			}
		}
	}
	
	@Override
	public void addFields() {
		for (int i = 0; i < 40; i++) {
			if (i == 0) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.GREEN, LudoFieldPos.ofGreen(i)));
			} else if (i == 10) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.YELLOW, LudoFieldPos.ofGreen(i)));
			} else if (i == 20) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.BLUE, LudoFieldPos.ofGreen(i)));
			} else if (i == 30) {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.RED, LudoFieldPos.ofGreen(i)));
			} else {
				this.addField(new LudoServerField(this, LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(i)));
			}
		}
		for (int i = 0; i < 16; i++) {
			this.homeFields.add(new LudoServerField(this, LudoFieldType.HOME, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
		for (int i = 0; i < 16; i++) {
			this.winFields.add(new LudoServerField(this, LudoFieldType.WIN, this.getFieldColor(i), LudoFieldPos.of(i % 4)));
		}
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
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		playerType = Utils.warpNullTo(playerType, LudoPlayerType.NO);
		if (fieldType == LudoFieldType.DEFAULT) {
			if (playerType != LudoPlayerType.NO && fieldPos.getPosition() % 10 == 0) {
				return this.getFields().get(fieldPos.getPosition());
			}
			return this.getFields().get(fieldPos.getPosition());
		} else if (playerType != LudoPlayerType.NO) {
			if (fieldType == LudoFieldType.HOME) {
				return this.getHomeFields(playerType).get(fieldPos.getPositionFor(playerType));
			} else if (fieldType == LudoFieldType.WIN) {
				return this.getWinFields(playerType).get(fieldPos.getPositionFor(playerType));
			}
		}
		GameMap.LOGGER.warn("Fail to get a field with type {} and color {} at position {}", fieldType, playerType, fieldPos.getPosition());
		return null;
	}
	
	@Override
	public GameField getNextField(GameFigure figure, int count) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		GamePlayerType playerType = figure.getPlayerType();
		GameField currentField = this.getField(figure);
		if (currentField != null) {
			if (count > 0) {
				if (currentField.getFieldType() == LudoFieldType.HOME) {
					if (count == 6) {
						return this.getStartFields(playerType).get(0);
					}
					return null;
				} else {
					int position = currentField.getFieldPos().getPositionFor(playerType) + count;
					if (currentField.getFieldType() == LudoFieldType.WIN) {
						if (position > 3) {
							return null;
						}
						return this.getWinFields(playerType).get(position);
					} else {
						if (position > 43) {
							return null;
						} else if (position > 39) {
							return this.getWinFields(playerType).get(position - 40);
						} else {
							return this.getFields().get(Objects.requireNonNull(LudoFieldPos.of(playerType, position)).getPosition());
						}
					}
				}
			}
			GameMap.LOGGER.warn("Fail to get next field for figure {} of player {}, since the count must be larger than 0 but it is {}", figure.getCount(), playerName, count);
			return currentField;
		}
		GameMap.LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), playerName);
		return null;
	}
	
	@Override
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case LudoPlayerType.GREEN -> this.homeFields.subList(0, 4);
			case LudoPlayerType.YELLOW -> this.homeFields.subList(4, 8);
			case LudoPlayerType.BLUE -> this.homeFields.subList(8, 12);
			case LudoPlayerType.RED -> this.homeFields.subList(12, 16);
			default -> {
				GameMap.LOGGER.warn("Fail to get home fields for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case LudoPlayerType.GREEN, LudoPlayerType.YELLOW, LudoPlayerType.BLUE, LudoPlayerType.RED ->
					Lists.newArrayList(this.getFields().get(Objects.requireNonNull(LudoFieldPos.of(playerType, 0)).getPosition()));
			default -> {
				GameMap.LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case LudoPlayerType.GREEN -> this.winFields.subList(0, 4);
			case LudoPlayerType.YELLOW -> this.winFields.subList(4, 8);
			case LudoPlayerType.BLUE -> this.winFields.subList(8, 12);
			case LudoPlayerType.RED -> this.winFields.subList(12, 16);
			default -> {
				GameMap.LOGGER.warn("Fail to get win fields for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public boolean moveFigureTo(GameFigure figure, GameField field) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
		if (field != null) {
			if (field.isEmpty()) {
				Objects.requireNonNull(this.getField(figure)).clear();
				field.setFigure(figure);
				return true;
			} else {
				GameFigure opponentFigure = field.getFigure();
				assert opponentFigure != null;
				String opponentName = opponentFigure.getPlayer().getPlayer().getProfile().getName();
				if (opponentFigure.isKickable()) {
					if (figure.canKick(opponentFigure)) {
						if (this.moveFigureTo(opponentFigure, this.getField(LudoFieldType.HOME, opponentFigure.getPlayerType(), opponentFigure.getHomePos()))) {
							Objects.requireNonNull(this.getField(figure)).clear();
							field.setFigure(figure);
							return true;
						} else {
							GameMap.LOGGER.warn("Fail to move figure {} of player {} to it's home field {}", opponentFigure.getCount(), opponentName, opponentFigure.getHomePos().getPosition());
							GameMap.LOGGER.warn("Can not move figure {} of player {} to field {}, since there was an error while moving the figure on the field home", figure.getCount(), playerName, field.getFieldPos().getPosition());
							return false;
						}
					} else {
						GameMap.LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable by the figure of the player", figure.getCount(), playerName, field.getFieldPos().getPosition());
						return false;
					}
				} else {
					GameMap.LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable", figure.getCount(), playerName, field.getFieldPos().getPosition());
					return false;
				}
			}
		}
		GameMap.LOGGER.warn("Can not move figure {} of player {}, since the new field is null", figure.getCount(), playerName);
		return false;
	}
	
	@Override
	public String toString() {
		return "LudoServerMap";
	}
	
}
