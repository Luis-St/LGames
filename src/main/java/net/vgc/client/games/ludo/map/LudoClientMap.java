package net.vgc.client.games.ludo.map;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.game.wrapper.GridPaneWrapper;
import net.vgc.client.game.map.AbstractClientGameMap;
import net.vgc.client.games.ludo.map.field.LudoClientField;
import net.vgc.client.games.ludo.player.LudoClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldPos;
import net.vgc.games.ludo.map.field.LudoFieldType;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class LudoClientMap extends AbstractClientGameMap implements GridPaneWrapper {
	
	private final ToggleGroup group;
	private final GridPane gridPane;
	private final List<GameField> homeFields = Lists.newArrayList();
	private final List<GameField> winFields = Lists.newArrayList();
	
	public LudoClientMap(Client client, Game game) {
		super(client, game);
		this.group = new ToggleGroup();
		this.gridPane = new GridPane();
		this.init();
		this.addFields();
	}
	
	@Override
	public GridPane getGridPane() {
		return this.gridPane;
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10.0);
		this.setVgap(10.0);
		this.setPadding(new Insets(20.0));
		this.setGridLinesVisible(Constans.DEBUG);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			LocalPlayer player = this.getClient().getPlayer();
			if (player.isCurrent() && player.canSelect() && Mth.isInBounds(player.getCount(), 1, 6)) {
				if (newValue instanceof LudoClientField field && field.canSelect() && field.getFigure().getPlayerType() == this.getGame().getPlayerType(this.getGame().getPlayerFor(player))) {
					
				} else {
					this.group.selectToggle(null);
				}
			} else {
				this.group.selectToggle(null);
			}
			this.getFields().stream().filter(GameField::isShadowed).forEach(GameField::clearShadow);
		});
	}
	
	@Override
	public void addFields() {
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.GREEN, LudoFieldPos.ofGreen(0), 0, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(1), 1, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(2), 2, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(3), 3, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(4), 4, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(5), 4, 3);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(6), 4, 2);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(7), 4, 1);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(8), 4, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(9), 5, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.YELLOW, LudoFieldPos.ofGreen(10), 6, 0);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(11), 6, 1);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(12), 6, 2);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(13), 6, 3);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(14), 6, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(15), 7, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(16), 8, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(17), 9, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(18), 10, 4);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(19), 10, 5);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.BLUE, LudoFieldPos.ofGreen(20), 10, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(21), 9, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(22), 8, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(23), 7, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(24), 6, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(25), 6, 7);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(26), 6, 8);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(27), 6, 9);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(28), 6, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(29), 5, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.RED, LudoFieldPos.ofGreen(30), 4, 10);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(31), 4, 9);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(32), 4, 8);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(33), 4, 7);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(34), 4, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(35), 3, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(36), 2, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(37), 1, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(38), 0, 6);
		this.addField(LudoFieldType.DEFAULT, LudoPlayerType.NO, LudoFieldPos.ofGreen(39), 0, 5);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(0), 0, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(1), 1, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(2), 1, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.GREEN, LudoFieldPos.of(3), 0, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(0), 9, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(1), 10, 0);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(2), 10, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.YELLOW, LudoFieldPos.of(3), 9, 1);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(0), 9, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(1), 10, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(3), 10, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.BLUE, LudoFieldPos.of(4), 9, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(0), 0, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(1), 1, 9);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(3), 1, 10);
		this.addField(LudoFieldType.HOME, LudoPlayerType.RED, LudoFieldPos.of(4), 0, 10);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(0), 1, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(1), 2, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(2), 3, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.GREEN, LudoFieldPos.of(3), 4, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(0), 5, 1);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(1), 5, 2);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(2), 5, 3);
		this.addField(LudoFieldType.WIN, LudoPlayerType.YELLOW, LudoFieldPos.of(3), 5, 4);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(3), 6, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(2), 7, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(1), 8, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.BLUE, LudoFieldPos.of(0), 9, 5);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(3), 5, 6);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(2), 5, 7);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(1), 5, 8);
		this.addField(LudoFieldType.WIN, LudoPlayerType.RED, LudoFieldPos.of(0), 5, 9);
	}
	
	protected void addField(LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos, int column, int row) {
		LudoClientField field = new LudoClientField(this.getClient(), this, this.group, fieldType, colorType, fieldPos, 67.0);
		field.setOnAction((event) -> {
			LocalPlayer player = this.getClient().getPlayer();
			if (player.isCurrent() && player.canSelect() && Mth.isInBounds(player.getCount(), 1, 6) && field.canSelect()) {
				GameFigure figure = field.getFigure();
				if (figure != null && figure.getPlayerType() == this.getGame().getPlayerType(this.getGame().getPlayerFor(player))) {
					GameField nextField = this.getNextField(figure, player.getCount());
					if (nextField != null) {
						nextField.setShadowed(true);
					}
				}
			}
		});
		this.add(field.getToggleButton(), column, row);
		if (fieldType == LudoFieldType.DEFAULT) {
			this.addField(field);
		} else if (fieldType == LudoFieldType.HOME) {
			this.homeFields.add(field);
		} else if (fieldType == LudoFieldType.WIN) {
			this.winFields.add(field);
		} else {
			LOGGER.warn("Fail to add field {} with type {} to ludo client map", fieldPos.getPosition(), fieldType);
		}
	}
	
	@Override
	public void init(List<GamePlayer> players) {
		super.init(players);
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer instanceof LudoClientPlayer player) {
				LOGGER.debug("Add figures ({}) of player {}, to their home fields", player.getFigures().size(), player.getPlayer().getProfile().getName());
				for (GameFigure figure : player.getFigures()) {
					GameField field = this.getField(LudoFieldType.HOME, player.getPlayerType(), figure.getHomePos());
					field.setFigure(figure);
				}
			} else {
				LOGGER.error("Can not add a game player of type {} to a ludo game", gamePlayer.getClass().getSimpleName());
				throw new RuntimeException("Can not add a game player of type " + gamePlayer.getClass().getSimpleName() + " to a ludo game");
			}
		}
	}
	
	@Override
	public GameField getField(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos) {
		playerType = Util.warpNullTo(playerType, LudoPlayerType.NO);
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
		LOGGER.warn("Fail to get a field with type {} and color {} at position {}", fieldType, playerType, fieldPos.getPosition());
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
							return this.getFields().get(LudoFieldPos.of(playerType, position).getPosition());
						}
					}
				}
			}
			LOGGER.warn("Fail to get next field for figure {} of player {}, since the count must be larger than 0 but it is {}", figure.getCount(), playerName, count);
			return currentField;
		}
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), playerName);
		return null;
	}
	
	@Override
	public List<GameField> getHomeFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN:
				return this.homeFields.subList(0, 4);
			case YELLOW:
				return this.homeFields.subList(4, 8);
			case BLUE:
				return this.homeFields.subList(8, 12);
			case RED:
				return this.homeFields.subList(12, 16);
			default:
				break;
		}
		LOGGER.warn("Fail to get home fields for type {}", playerType);
		return Lists.newArrayList();
	}
	
	@Override
	public List<GameField> getStartFields(GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN, YELLOW, BLUE, RED -> Lists.newArrayList(this.getFields().get(LudoFieldPos.of((LudoPlayerType) playerType, 0).getPosition()));
			default -> {
				LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public List<GameField> getWinFields(GamePlayerType playerType) {
		switch ((LudoPlayerType) playerType) {
			case GREEN:
				return this.winFields.subList(0, 4);
			case YELLOW:
				return this.winFields.subList(4, 8);
			case BLUE:
				return this.winFields.subList(8, 12);
			case RED:
				return this.winFields.subList(12, 16);
			default:
				break;
		}
		LOGGER.warn("Fail to get win fields for type {}", playerType);
		return Lists.newArrayList();
	}
	
	@Override
	public GameField getSelectedField() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof LudoClientField field) {
			if (!field.isEmpty()) {
				return field;
			} else {
				LOGGER.warn("Fail to get the selected field, since the selected field does not have a figure on it");
				return null;
			}
		}
		LOGGER.info("Fail to get the selected field, since there is no field selected");
		return null;
	}
	
	/*
	 * @Override
	 * public void handlePacket(ClientPacket clientPacket) {
	 * if (clientPacket instanceof UpdateGameMapPacket packet) {
	 * for (GameFieldInfo fieldInfo : packet.getFieldInfos()) {
	 * GameProfile profile = fieldInfo.getProfile();
	 * if (fieldInfo.getFieldPos() instanceof LudoFieldPos fieldPos) {
	 * LudoClientField field = this.getField(fieldInfo.getFieldType(), fieldInfo.getPlayerType(), fieldPos);
	 * if (field != null) {
	 * if (field.isShadowed()) {
	 * field.setShadowed(false);
	 * }
	 * LudoClientPlayer player = (LudoClientPlayer) this.game.getPlayerFor(profile);
	 * if (player != null) {
	 * LudoClientFigure figure = player.getFigure(fieldInfo.getFigureCount());
	 * UUID uuid = figure.getUUID();
	 * UUID serverUUID = fieldInfo.getFigureUUID();
	 * if (uuid.equals(serverUUID)) {
	 * field.setFigure(figure);
	 * } else {
	 * LOGGER.warn("Fail to place figure {} of player {} at field {}, since the figure uuid {} does not match with the server on {}", figure.getCount(), profile.getName(), fieldPos.getPosition(),
	 * uuid, serverUUID);
	 * }
	 * } else if (profile.equals(GameProfile.EMPTY)) {
	 * field.setFigure(null);
	 * } else {
	 * LOGGER.warn("Fail to place a figure of player {} at field {}, since the player does not exsists", profile.getName(), fieldPos.getPosition());
	 * }
	 * } else {
	 * LOGGER.warn("Fail to update game field, since there is not field for pos {}", fieldPos.getPosition());
	 * }
	 * } else {
	 * LOGGER.warn("Fail to update game field, since field pos is a instance of {}", fieldInfo.getFieldPos().getClass().getSimpleName());
	 * }
	 * }
	 * }
	 * }
	 */
	
	@Override
	public String toString() {
		return "LudoClientMap";
	}
	
}
