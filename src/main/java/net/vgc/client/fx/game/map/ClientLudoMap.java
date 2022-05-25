package net.vgc.client.fx.game.map;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import net.vgc.Constans;
import net.vgc.client.Client;
import net.vgc.client.fx.game.LudoButton;
import net.vgc.game.ludo.LudoState;
import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;
import net.vgc.game.ludo.player.LudoFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.client.game.UpdateLudoGamePacket;

public class ClientLudoMap extends GridPane implements PacketHandler<ClientPacket> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final Client client;
	protected final Supplier<Boolean> canSelectField;
	protected final Supplier<Integer> diceCountGetter;
	protected final ToggleGroup group;
	protected final List<LudoButton> fieldButtons = Lists.newArrayList();
	protected final List<LudoButton> homeFieldButtons = Lists.newArrayList();
	protected final List<LudoButton> winFieldButtons = Lists.newArrayList();
	
	public ClientLudoMap(Client client, Supplier<Boolean> canSelectField, Supplier<Integer> diceCountGetter) {
		this.client = client;
		this.canSelectField = canSelectField;
		this.diceCountGetter = diceCountGetter;
		this.group = new ToggleGroup();
		this.init();
		this.createFields();
	}
	
	protected void init() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10.0);
		this.setVgap(10.0);
		this.setPadding(new Insets(20.0));
		this.setGridLinesVisible(Constans.DEBUG);
		this.group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (!this.canSelectField.get()) {
				this.group.selectToggle(null);
			}
			this.clearShadow();
		});
	}
	
	protected void createFields() {
		this.add(this.makeGreenField(LudoFieldType.DEFAULT, LudoFieldPos.ofGreen(0)), 0, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(1)), 1, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(2)), 2, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(3)), 3, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(4)), 4, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(5)), 4, 3);
		this.add(this.makeField(LudoFieldPos.ofGreen(6)), 4, 2);
		this.add(this.makeField(LudoFieldPos.ofGreen(7)), 4, 1);
		this.add(this.makeField(LudoFieldPos.ofGreen(8)), 4, 0);
		this.add(this.makeField(LudoFieldPos.ofGreen(9)), 5, 0);
		this.add(this.makeYellowField(LudoFieldType.DEFAULT, LudoFieldPos.ofGreen(10)), 6, 0);
		this.add(this.makeField(LudoFieldPos.ofGreen(11)), 6, 1);
		this.add(this.makeField(LudoFieldPos.ofGreen(12)), 6, 2);
		this.add(this.makeField(LudoFieldPos.ofGreen(13)), 6, 3); 
		this.add(this.makeField(LudoFieldPos.ofGreen(14)), 6, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(15)), 7, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(16)), 8, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(17)), 9, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(18)), 10, 4);
		this.add(this.makeField(LudoFieldPos.ofGreen(19)), 10, 5);
		this.add(this.makeBlueField(LudoFieldType.DEFAULT, LudoFieldPos.ofGreen(20)), 10, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(21)), 9, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(22)), 8, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(23)), 7, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(24)), 6, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(25)), 6, 7);
		this.add(this.makeField(LudoFieldPos.ofGreen(26)), 6, 8);
		this.add(this.makeField(LudoFieldPos.ofGreen(27)), 6, 9);
		this.add(this.makeField(LudoFieldPos.ofGreen(28)), 6, 10);
		this.add(this.makeField(LudoFieldPos.ofGreen(29)), 5, 10);
		this.add(this.makeRedField(LudoFieldType.DEFAULT, LudoFieldPos.ofGreen(30)), 4, 10);
		this.add(this.makeField(LudoFieldPos.ofGreen(31)), 4, 9);
		this.add(this.makeField(LudoFieldPos.ofGreen(32)), 4, 8);
		this.add(this.makeField(LudoFieldPos.ofGreen(33)), 4, 7);
		this.add(this.makeField(LudoFieldPos.ofGreen(34)), 4, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(35)), 3, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(36)), 2, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(37)), 1, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(38)), 0, 6);
		this.add(this.makeField(LudoFieldPos.ofGreen(39)), 0, 5);
		this.add(this.makeGreenField(LudoFieldType.HOME, LudoFieldPos.of(0)), 0, 0);
		this.add(this.makeGreenField(LudoFieldType.HOME, LudoFieldPos.of(1)), 1, 0);
		this.add(this.makeGreenField(LudoFieldType.HOME, LudoFieldPos.of(2)), 1, 1);
		this.add(this.makeGreenField(LudoFieldType.HOME, LudoFieldPos.of(3)), 0, 1);
		this.add(this.makeYellowField(LudoFieldType.HOME, LudoFieldPos.of(0)), 9, 0);
		this.add(this.makeYellowField(LudoFieldType.HOME, LudoFieldPos.of(1)), 10, 0);
		this.add(this.makeYellowField(LudoFieldType.HOME, LudoFieldPos.of(2)), 10, 1);
		this.add(this.makeYellowField(LudoFieldType.HOME, LudoFieldPos.of(3)), 9, 1);
		this.add(this.makeBlueField(LudoFieldType.HOME, LudoFieldPos.of(0)), 9, 9);
		this.add(this.makeBlueField(LudoFieldType.HOME, LudoFieldPos.of(1)), 10, 9);
		this.add(this.makeBlueField(LudoFieldType.HOME, LudoFieldPos.of(3)), 10, 10);
		this.add(this.makeBlueField(LudoFieldType.HOME, LudoFieldPos.of(4)), 9, 10);
		this.add(this.makeRedField(LudoFieldType.HOME, LudoFieldPos.of(0)), 0, 9);
		this.add(this.makeRedField(LudoFieldType.HOME, LudoFieldPos.of(1)), 1, 9);
		this.add(this.makeRedField(LudoFieldType.HOME, LudoFieldPos.of(3)), 1, 10);
		this.add(this.makeRedField(LudoFieldType.HOME, LudoFieldPos.of(4)), 0, 10);
		this.add(this.makeGreenField(LudoFieldType.WIN, LudoFieldPos.of(0)), 1, 5);
		this.add(this.makeGreenField(LudoFieldType.WIN, LudoFieldPos.of(1)), 2, 5);
		this.add(this.makeGreenField(LudoFieldType.WIN, LudoFieldPos.of(2)), 3, 5);
		this.add(this.makeGreenField(LudoFieldType.WIN, LudoFieldPos.of(3)), 4, 5);
		this.add(this.makeYellowField(LudoFieldType.WIN, LudoFieldPos.of(0)), 5, 1);
		this.add(this.makeYellowField(LudoFieldType.WIN, LudoFieldPos.of(1)), 5, 2);
		this.add(this.makeYellowField(LudoFieldType.WIN, LudoFieldPos.of(2)), 5, 3);
		this.add(this.makeYellowField(LudoFieldType.WIN, LudoFieldPos.of(3)), 5, 4);
		this.add(this.makeBlueField(LudoFieldType.WIN, LudoFieldPos.of(3)), 6, 5);
		this.add(this.makeBlueField(LudoFieldType.WIN, LudoFieldPos.of(2)), 7, 5);
		this.add(this.makeBlueField(LudoFieldType.WIN, LudoFieldPos.of(1)), 8, 5);
		this.add(this.makeBlueField(LudoFieldType.WIN, LudoFieldPos.of(0)), 9, 5);
		this.add(this.makeRedField(LudoFieldType.WIN, LudoFieldPos.of(3)), 5, 6);
		this.add(this.makeRedField(LudoFieldType.WIN, LudoFieldPos.of(2)), 5, 7);
		this.add(this.makeRedField(LudoFieldType.WIN, LudoFieldPos.of(1)), 5, 8);
		this.add(this.makeRedField(LudoFieldType.WIN, LudoFieldPos.of(0)), 5, 9);
	}
	
	protected LudoButton makeField(LudoFieldPos pos) {
		return this.makeButton("textures/ludo/field/field.png", LudoFieldType.DEFAULT, pos);
	}
	
	protected LudoButton makeGreenField(LudoFieldType type, LudoFieldPos pos) {
		return this.makeButton("textures/ludo/field/green_field.png", type, pos);
	}
	
	protected LudoButton makeYellowField(LudoFieldType type, LudoFieldPos pos) {
		return this.makeButton("textures/ludo/field/yellow_field.png", type, pos);
	}
	
	protected LudoButton makeBlueField(LudoFieldType type, LudoFieldPos pos) {
		return this.makeButton("textures/ludo/field/blue_field.png", type, pos);
	}
	
	protected LudoButton makeRedField(LudoFieldType type, LudoFieldPos pos) {
		return this.makeButton("textures/ludo/field/red_field.png", type, pos);
	}
	
	protected LudoButton makeButton(String fieldPath, LudoFieldType type, LudoFieldPos pos) {
		LudoButton button = new LudoButton(this.client, this.group, fieldPath, type, pos, 75.0);
		button.setOnAction((event) -> {
			if (this.canSelectField.get()) {
				if (button.canSelect()) {
					this.clearAndSetShadow(this.getNextField(button, this.diceCountGetter.get(), false));
				}
			}
		});
		if (type == LudoFieldType.DEFAULT) {
			this.fieldButtons.add(button);
		} else if (type == LudoFieldType.HOME) {
			this.homeFieldButtons.add(button);
		} else if (type == LudoFieldType.WIN) {
			this.winFieldButtons.add(button);
		}
		return button;
	}
	
	protected List<LudoButton> getAllFieldButtons() {
		List<LudoButton> fieldButtons = Lists.newArrayList();
		fieldButtons.addAll(this.homeFieldButtons);
		fieldButtons.addAll(this.fieldButtons);
		fieldButtons.addAll(this.winFieldButtons);
		return fieldButtons;
	}
	
	@Nullable
	protected LudoButton getNextField(LudoButton fieldButton, int count, boolean returnCall) {
		LudoType type = fieldButton.getType();
		LudoFieldType fieldType = fieldButton.getFieldType();
		if (fieldType == LudoFieldType.HOME) {
			if (count == 6) {
				return this.checkField(this.getButton(LudoFieldType.DEFAULT, LudoFieldPos.of(type, 0), null));
			} else {
				return null;
			}
		} else if (fieldType == LudoFieldType.WIN) {
			int position = fieldButton.getPos().getFieldForType(type) + count;
			if (position > 3) {
				LOGGER.warn("The next field for count {}, is out of map", count);
				return null;
			}
			return this.checkField(this.getButton(LudoFieldType.WIN, LudoFieldPos.of(position), type));
		} else {
			if (this.hasFigureAtHome() && !returnCall) {
				if (count == 6 && !this.hasFigureAtStart()) {
					if (fieldType == LudoFieldType.HOME) {
						return this.checkField(this.getButton(LudoFieldType.DEFAULT, LudoFieldPos.of(type, 0), null));
					} else {
						return null;
					}
				} else if (this.hasFigureAtStart()) {
					if (fieldType == LudoFieldType.DEFAULT) {
						LudoButton nextStartFieldButton = this.getButton(LudoFieldType.DEFAULT, LudoFieldPos.of(type, count), null);
						if (fieldButton.getPos().isStart() && !nextStartFieldButton.hasFigure()) {
							return nextStartFieldButton;
						} else if (nextStartFieldButton.hasFigure() && nextStartFieldButton.getType() == this.client.getPlayer().getType()) {
							return this.getNextField(fieldButton, count, true);
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			}
			int position = fieldButton.getPos().getFieldForType(type) + count;
			if (position > 39) {
				if (position > 43) {
					LOGGER.warn("The next field for count {}, is out of map", count);
					return null;
				}
				return this.checkField(this.getButton(LudoFieldType.WIN, LudoFieldPos.of(position - 40), type));
			} else {
				return this.checkField(this.getButton(LudoFieldType.DEFAULT, LudoFieldPos.of(type, position), null));
			}
		}
	}
	
	protected LudoButton checkField(LudoButton fieldButton) {
		if (fieldButton == null) {
			return null;
		} else if (this.hasFigureAtHome() && fieldButton.getFieldType() == LudoFieldType.HOME && fieldButton.getPos().isHomeOrWin()) {
			return null;
		} else if (fieldButton.hasFigure()) {
			LudoType type = fieldButton.getType();
			if (type == this.client.getPlayer().getType()) {
				return null;
			} else {
				return fieldButton;
			}
		}
		return fieldButton;
	}
	
	protected boolean hasFigureAtHome() {
		for (LudoButton fieldButton : this.getFiguresForType((LudoType) this.client.getPlayer().getType())) {
			if (fieldButton.getFieldType() == LudoFieldType.HOME) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean hasFigureAtStart() {
		LudoType type = (LudoType) this.client.getPlayer().getType();
		for (LudoButton fieldButton : this.getFiguresForType(type)) {
			if (fieldButton.getFieldType() == LudoFieldType.DEFAULT && fieldButton.getPos().isStart() && fieldButton.getPos().getFieldForType(type) == 0) {
				return true;
			}
		}
		return false;
	}
	
	protected List<LudoButton> getFiguresForType(LudoType type) {
		return this.getAllFieldButtons().stream().filter((fieldButton) -> {
			return fieldButton.getType() == type;
		}).collect(Collectors.toList());
	}
	
	protected void clearAndSetShadow(LudoButton fieldButton) {
		this.clearShadow();
		if (fieldButton != null) {
			fieldButton.setTypeAndState(LudoType.NO, LudoState.SHADOW);
		} else {
			LOGGER.warn("Fail to display shadow figure, since there is no next field");
		}
	}
	
	protected void clearShadow() {
		this.getAllFieldButtons().forEach((button) -> {
			if (button.getType() == LudoType.NO && button.getState() == LudoState.SHADOW) {
				button.reset();
			}
		});
	}
	
	@Override
	public void handlePacket(ClientPacket clientPacket) {
		if (clientPacket instanceof UpdateLudoGamePacket packet) {
			this.updateFigures(packet.getFigurePositions());
			this.group.selectToggle(null);
		}
	}
	
	protected void updateFigures(List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> figurePositions) {
		this.getAllFieldButtons().forEach((button) -> {
			button.setTypeAndState(LudoType.NO, LudoState.DEFAULT);
		});
		for (Cell<LudoFieldType, LudoFieldPos, LudoFigure> figurePosition : figurePositions) {
			LudoFigure figure = figurePosition.getValue();
			LudoButton button = this.getButton(figurePosition.getRowKey(), figurePosition.getColumnKey(), figure.getType());
			if (button != null) {
				button.setTypeAndState(figure.getType(), LudoState.DEFAULT);
			} else {
				LOGGER.warn("Fail to set ludo figure of player {} with type {} to field {} of field type {}", figure.getProfile().getName(), figure.getType(), figurePosition.getColumnKey().getGreen(), figurePosition.getRowKey());
			}
		}
	}
	
	@Nullable
	protected LudoButton getButton(LudoFieldType fieldType, LudoFieldPos pos, @Nullable LudoType type) {
		LudoButton button = null;
		if (fieldType == LudoFieldType.DEFAULT) {
			button = this.fieldButtons.get(pos.getGreen());
		} else if (fieldType == LudoFieldType.HOME) {
			button = this.homeFieldButtons.get(pos.getGreen() + 4 * type.ordinal());
		} else if (fieldType == LudoFieldType.WIN) {
			button = this.winFieldButtons.get(pos.getGreen() + 4 * type.ordinal());
		}
		if (button == null) {
			LOGGER.warn("Fail to get ludo field for pos {} and field type {}", pos.getGreen(), fieldType);
		}
		return button;
	}
	
	@Nullable
	public LudoFieldPos getSelectedFigure() {
		Toggle toggle = this.group.getSelectedToggle();
		if (toggle instanceof LudoButton button) {
			if (button.hasFigure()) {
				return button.getPos();
			} else {
				LOGGER.warn("Fail to get the selected figure, since the selected field does not have a figure on it");
				return null;
			}
		}
		LOGGER.warn("Fail to get the selected figure, since no field is selected");
		return null;
	}
	
}
