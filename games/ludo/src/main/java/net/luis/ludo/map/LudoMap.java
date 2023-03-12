package net.luis.ludo.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import net.luis.Constants;
import net.luis.fx.game.wrapper.GridPaneWrapper;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.Game;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.ludo.LudoGame;
import net.luis.ludo.map.field.LudoField;
import net.luis.ludo.map.field.LudoFieldPos;
import net.luis.ludo.map.field.LudoFieldType;
import net.luis.ludo.player.LudoPlayerType;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.listener.PacketListener;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 *
 * @author Luis-st
 *
 */

public class LudoMap extends AbstractGameMap implements GridPaneWrapper {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoGame.class);
	
	private final ToggleGroup group = new ToggleGroup();
	private final GridPane gridPane = new GridPane();
	private final List<GameField> homeFields = Lists.newArrayList();
	private final List<GameField> winFields = Lists.newArrayList();
	
	public LudoMap(Game game) {
		super(game);
	}
	
	@Override
	public @NotNull GridPane getGridPane() {
		return this.gridPane;
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10.0);
		this.setVgap(10.0);
		this.setPadding(new Insets(20.0));
		this.setGridLinesVisible(Constants.DEBUG_MODE);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			Player player = Objects.requireNonNull(this.getGame().getPlayer()).getPlayer();
			if (player.isCurrent() && player.canSelect() && Mth.isInBounds(player.getCount(), 1, 6)) {
				if (newValue instanceof ToggleButton button && button.getUserData() instanceof LudoField field) {
					if (!field.canSelect() || Objects.requireNonNull(field.getFigure()).getPlayerType() != this.getGame().getPlayerType(this.getGame().getPlayerFor(player))) {
						this.group.selectToggle(null);
					}
				} else {
					this.group.selectToggle(null);
				}
			} else {
				this.group.selectToggle(null);
			}
			this.getFields().stream().filter(GameField::isShadowed).forEach((field) -> field.setShadowed(false));
		});
	}
	
	@Override
	public void init(@NotNull List<GamePlayer> players) {
		for (GamePlayer gamePlayer : players) {
			LOGGER.debug("Add figures ({}) of player {}, to their home fields", gamePlayer.getFigures().size(), gamePlayer.getPlayer().getProfile().getName());
			for (GameFigure figure : gamePlayer.getFigures()) {
				Objects.requireNonNull(this.getField(LudoFieldType.HOME, gamePlayer.getPlayerType(), figure.getHomePos())).setFigure(figure);
			}
		}
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
	
	private void addField(LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos, int column, int row) {
		LudoField field = new LudoField(this, this.group, fieldType, colorType, fieldPos, 67.0);
		field.setOnAction((event) -> {
			Player player = Objects.requireNonNull(this.getGame().getPlayer()).getPlayer();
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
	public @NotNull List<GameField> getFields() {
		return Stream.of(super.getFields(), this.homeFields, this.winFields).flatMap(List::stream).collect(ImmutableList.toImmutableList());
	}
	
	@Override
	public @Nullable GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, @NotNull GameFieldPos fieldPos) {
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
		LOGGER.warn("Fail to get a field with type {} and color {} at position {}", fieldType, playerType, fieldPos.getPosition());
		return null;
	}
	
	@Override
	public @Nullable GameField getNextField(@NotNull GameFigure figure, int count) {
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
			LOGGER.warn("Fail to get next field for figure {} of player {}, since the count must be larger than 0 but it is {}", figure.getCount(), figure.getPlayer().getName(), count);
			return currentField;
		}
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), figure.getPlayer().getName());
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
	public @NotNull List<GameField> getHomeFields(@NotNull GamePlayerType playerType) {
		return this.getFieldsFrom(playerType, this.homeFields);
	}
	
	@Override
	public @NotNull List<GameField> getStartFields(@NotNull GamePlayerType playerType) {
		return switch ((LudoPlayerType) playerType) {
			case GREEN, YELLOW, BLUE, RED -> Lists.newArrayList(this.getFields().get(Objects.requireNonNull(LudoFieldPos.of(playerType, 0)).getPosition()));
			default -> {
				LOGGER.warn("Fail to get start field for type {}", playerType);
				yield Lists.newArrayList();
			}
		};
	}
	
	@Override
	public @NotNull List<GameField> getWinFields(@NotNull GamePlayerType playerType) {
		return this.getFieldsFrom(playerType, this.winFields);
	}
	
	@Override
	public boolean moveFigureTo(@NotNull GameFigure figure, @NotNull GameField field) {
		String playerName = figure.getPlayer().getPlayer().getProfile().getName();
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
					if (this.moveFigureTo(opponentFigure, Objects.requireNonNull(this.getField(LudoFieldType.HOME, opponentFigure.getPlayerType(), opponentFigure.getHomePos())))) {
						Objects.requireNonNull(this.getField(figure)).clear();
						field.setFigure(figure);
						return true;
					} else {
						LOGGER.warn("Fail to move figure {} of player {} to it's home field {}", opponentFigure.getCount(), opponentName, opponentFigure.getHomePos().getPosition());
						LOGGER.warn("Can not move figure {} of player {} to field {}, since there was an error while moving the figure on the field home", figure.getCount(), playerName, field.getFieldPos().getPosition());
						return false;
					}
				} else {
					LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable by the figure of the player", figure.getCount(), playerName, field.getFieldPos().getPosition());
					return false;
				}
			} else {
				LOGGER.warn("Can not move figure {} of player {} to field {}, since the figure on the field is not kickable", figure.getCount(), playerName, field.getFieldPos().getPosition());
				return false;
			}
		}
	}
	
	@Override
	public GameField getSelectedField() {
		for (GameField field : this.getFields()) {
			if (field instanceof ToggleButtonWrapper wrapper && wrapper.getToggleButton() == this.group.getSelectedToggle()) {
				if (!field.isEmpty()) {
					return field;
				} else {
					LOGGER.warn("Fail to get the selected field, since the selected field does not have a figure on it");
					return null;
				}
			}
		}
		LOGGER.info("Fail to get the selected field, since there is no field selected");
		return null;
	}
	
	@PacketListener(ClientPacket.class)
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof UpdateGameMapPacket packet) {
			for (GameFieldInfo fieldInfo : packet.getFieldInfos().stream().map(GameFieldInfo.class::cast).toList()) {
				GameProfile profile = fieldInfo.getProfile();
				GameFieldPos fieldPos = fieldInfo.getFieldPos();
				GameField field = this.getField(fieldInfo.getFieldType(), fieldInfo.getPlayerType(), fieldPos);
				if (field != null) {
					if (field.isShadowed()) {
						field.setShadowed(false);
					}
					GamePlayer player = this.getGame().getPlayerFor(profile);
					if (player != null) {
						GameFigure figure = player.getFigure(fieldInfo.getFigureCount());
						assert figure != null;
						UUID uuid = figure.getUniqueId();
						UUID serverUUID = fieldInfo.getFigureUUID();
						if (uuid.equals(serverUUID)) {
							field.setFigure(figure);
						} else {
							LOGGER.warn("Fail to place figure {} of player {} at field {}, since the figure uuid {} does not match with the server on {}", figure.getCount(), profile.getName(), fieldPos.getPosition(), uuid, serverUUID);
						}
					} else if (profile.equals(GameProfile.EMPTY)) {
						field.setFigure(null);
					} else {
						LOGGER.warn("Fail to place a figure of player {} at field {}, since the player does not exists", profile.getName(), fieldPos.getPosition());
					}
				} else {
					LOGGER.warn("Fail to update game field, since there is not field for pos {}", fieldPos.getPosition());
				}
			}
		}
	}
	
}
