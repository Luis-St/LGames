package net.luis.ttt.map;

import com.google.common.collect.Lists;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.luis.Constants;
import net.luis.fx.game.wrapper.GridPaneWrapper;
import net.luis.fx.game.wrapper.ToggleButtonWrapper;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.game.win.GameResultLine;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.ttt.map.field.TTTField;
import net.luis.ttt.map.field.TTTFieldPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */


public class TTTMap extends AbstractGameMap implements GridPaneWrapper {
	
	private static final Logger LOGGER = LogManager.getLogger(TTTMap.class);
	
	private final ToggleGroup group = new ToggleGroup();
	private final GridPane gridPane = new GridPane();
	
	public TTTMap(Game game) {
		super(game);
		this.addBorders();
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
			if (oldValue instanceof ToggleButton oldButton && oldButton.getUserData() instanceof TTTField oldField) {
				if (!(newValue instanceof ToggleButton newButton && newButton.getUserData() instanceof TTTField)) {
					oldField.setSelected(true);
				}
			}
			this.getFields().stream().filter(GameField::isShadowed).forEach((field) -> field.setShadowed(false));
		});
	}
	
	@Override
	public void init(@NotNull List<GamePlayer> players) {
		this.getFields().forEach(GameField::clear);
	}
	
	@Override
	public void addFields() {
		this.addField(TTTFieldPos.of(0), 0, 0);
		this.addField(TTTFieldPos.of(1), 2, 0);
		this.addField(TTTFieldPos.of(2), 4, 0);
		this.addField(TTTFieldPos.of(3), 0, 2);
		this.addField(TTTFieldPos.of(4), 2, 2);
		this.addField(TTTFieldPos.of(5), 4, 2);
		this.addField(TTTFieldPos.of(6), 0, 4);
		this.addField(TTTFieldPos.of(7), 2, 4);
		this.addField(TTTFieldPos.of(8), 4, 4);
	}
	
	private void addField(TTTFieldPos fieldPos, int column, int row) {
		TTTField field = new TTTField(this.getGame(), this.group, fieldPos);
		field.setOnAction((event) -> {
			if (!Objects.requireNonNull(this.getGame().getPlayer()).getPlayer().isCurrent()) {
				if (field.isEmpty() && field.getResult() == GameResult.NO) {
					field.setShadowed(true);
				}
			} else {
				this.group.selectToggle(null);
			}
		});
		this.add(field.getToggleButton(), column, row);
		this.addField(field);
	}
	
	public void addBorders() {
		this.addBorder(10, 160, 1, 0);
		this.addBorder(10, 160, 3, 0);
		this.addBorder(160, 10, 0, 1);
		this.addBorder(160, 10, 2, 1);
		this.addBorder(160, 10, 4, 1);
		this.addBorder(10, 160, 1, 2);
		this.addBorder(10, 160, 3, 2);
		this.addBorder(160, 10, 0, 3);
		this.addBorder(160, 10, 2, 3);
		this.addBorder(160, 10, 4, 3);
		this.addBorder(10, 160, 1, 4);
		this.addBorder(10, 160, 3, 4);
	}
	
	private void addBorder(int width, int height, int column, int row) {
		this.add(new Rectangle(width, height, Color.BLACK), column, row);
	}
	
	
	@Override
	public @Nullable GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, @NotNull GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}
	
	@Override
	public @Nullable GameField getNextField(@NotNull GameFigure figure, int count) {
		return null;
	}
	
	@Override
	public @NotNull List<GameField> getHomeFields(@NotNull GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public @NotNull List<GameField> getStartFields(@NotNull GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public @NotNull List<GameField> getWinFields(@NotNull GamePlayerType playerType) {
		return Lists.newArrayList();
	}
	
	@Override
	public GameField getSelectedField() {
		Toggle toggle = this.group.getSelectedToggle();
		for (GameField field : this.getFields()) {
			if (field instanceof ToggleButtonWrapper wrapper && wrapper.getToggleButton() == toggle) {
				if (field.isEmpty()) {
					return field;
				} else {
					LOGGER.warn("Fail to get the selected field, since the selected field can not have a figure on it");
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
				GameField field = this.getField(null, null, fieldPos);
				if (field != null) {
					if (field.isShadowed()) {
						field.setShadowed(false);
					}
					if (field.getResult() != GameResult.NO) {
						field.setResult(GameResult.NO);
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
							LOGGER.warn("Fail to place figure {} of player {} at field {}, since the figure uuid {} does not match with the server on {}", figure.getIndex(), profile.getName(), fieldPos.getPosition(), uuid, serverUUID);
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
		} else if (clientPacket instanceof GameResultPacket packet) {
			GameResult result = (GameResult) packet.getResult();
			if (result != GameResult.NO) {
				GameResultLine resultLine = packet.getObject().cast(GameResultLine.class);
				if (result == GameResult.DRAW) {
					for (GameField field : this.getFields()) {
						field.setResult(GameResult.DRAW);
					}
				} else {
					if (resultLine != GameResultLine.EMPTY) {
						assert resultLine != null;
						for (GameFieldPos fieldPos : resultLine.getPositions()) {
							Objects.requireNonNull(this.getField(null, null, fieldPos)).setResult(result);
						}
					} else {
						LOGGER.warn("Fail to handle game result {}, since there is no result line", result);
					}
				}
			} else {
				LOGGER.warn("Fail to handle game result {}", result);
			}
		}
	}
	
}
