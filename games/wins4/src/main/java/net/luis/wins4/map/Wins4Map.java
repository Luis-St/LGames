package net.luis.wins4.map;

import com.google.common.collect.Lists;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import net.luis.fx.game.IndexToggleButton;
import net.luis.fx.game.wrapper.StackPaneWrapper;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.GameResult;
import net.luis.game.map.AbstractGameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.listener.PacketListener;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import net.luis.wins4.map.field.Wins4Field;
import net.luis.wins4.map.field.Wins4FieldPos;
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

public class Wins4Map extends AbstractGameMap implements StackPaneWrapper {
	
	private final ToggleGroup group = new ToggleGroup();
	private final StackPane stackPane = new StackPane();
	private final GridPane fieldPane = FxUtils.makeGrid(Pos.CENTER, 0.0, 0.0);
	private final GridPane interactionPane = FxUtils.makeGrid(Pos.CENTER, 0.0, 0.0);
	
	public Wins4Map(Game game) {
		super(game);
		this.addInteractions();
		this.mergePanes();
	}
	
	@Override
	public @NotNull StackPane getStackPane() {
		return this.stackPane;
	}
	
	@Override
	public void init() {
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(20.0));
	}
	
	@Override
	public void init(@NotNull List<GamePlayer> players) {
		this.getFields().forEach(GameField::clear);
	}
	
	@Override
	public void addFields() {
		for (int i = 0; i < 42; i++) {
			this.addField(Wins4FieldPos.of(i));
		}
	}
	
	private void addField(Wins4FieldPos fieldPos) {
		Wins4Field field = new Wins4Field(this, fieldPos);
		this.fieldPane.add(field.getLabel(), fieldPos.getColumn(), fieldPos.getRow());
		this.addField(field);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			this.getFields().stream().filter(GameField::isShadowed).forEach((gameField) -> gameField.setShadowed(false));
		});
	}
	
	public void addInteractions() {
		for (int i = 0; i < 7; i++) {
			this.addInteraction(i, i);
		}
	}
	
	private void addInteraction(int index, int column) {
		IndexToggleButton button = new IndexToggleButton(index);
		button.setToggleGroup(this.group);
		button.setBackground(null);
		button.setPrefSize(120.0, 6120.0);
		button.setOnAction((event) -> {
			if (!Objects.requireNonNull(this.getGame().getPlayer()).getPlayer().isCurrent()) {
				List<GameField> fields = Utils.reverseList(this.getFieldsForColumn(index));
				for (GameField field : fields) {
					if (field.isEmpty()) {
						field.setShadowed(true);
						break;
					}
				}
			} else {
				this.getFields().stream().filter(GameField::isShadowed).forEach((field) -> field.setShadowed(false));
				this.group.selectToggle(null);
			}
		});
		this.interactionPane.add(button, column, 0);
	}
	
	private void mergePanes() {
		this.getChildren().addAll(this.fieldPane, this.interactionPane);
	}
	
	@Override
	public @Nullable GameField getField(@Nullable GameFieldType fieldType, @Nullable GamePlayerType playerType, @NotNull GameFieldPos fieldPos) {
		return this.getFields().get(fieldPos.getPosition());
	}
	
	@Override
	public @Nullable GameField getNextField(@NotNull GameFigure figure, int count) {
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
	
	public int getSelectedColumn() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof IndexToggleButton button) {
			return button.getIndex();
		}
		LOGGER.info("Fail to get the selected column, since there is no column selected");
		return -1;
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
		} else if (clientPacket instanceof GameResultPacket packet) {
			GameResult result = (GameResult) packet.getResult();
			if (result != GameResult.NO) {
				for (GameField field : this.getFields()) {
					field.setResult(result);
				}
			} else {
				LOGGER.warn("Fail to handle game result {}", result);
			}
		}
	}
	
}
