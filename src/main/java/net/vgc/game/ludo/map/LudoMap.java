package net.vgc.game.ludo.map;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.map.field.LudoField;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;
import net.vgc.game.ludo.player.LudoFigure;
import net.vgc.game.ludo.player.LudoPlayer;
import net.vgc.util.SimpleCell;
import net.vgc.util.Util;

public class LudoMap {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final List<LudoField> fields = Util.make(Lists.newArrayList(), LudoMapUtil::createDefaultFields);
	protected final List<LudoField> homeFields = Util.make(Lists.newArrayList(), LudoMapUtil::createHomeFields);
	protected final List<LudoField> winFields = Util.make(Lists.newArrayList(), LudoMapUtil::createWinFields);
	
	public void init(List<LudoPlayer> players) {
		this.fields.forEach(LudoField::clearField);
		this.homeFields.forEach(LudoField::clearField);
		this.winFields.forEach(LudoField::clearField);
		for (LudoPlayer player : players) {
			for (LudoFigure figure : player.getFigures()) {
				this.homeFields.get(figure.getCount() + 4 * figure.getType().ordinal()).setFigure(figure);
			}
		}
	}
	
	public LudoField getField(LudoFieldType fieldType, @Nullable LudoType type, LudoFieldPos pos) {
		if (type != null) {
			if (fieldType == LudoFieldType.HOME) {
				return this.homeFields.get(pos.getFieldForType(type));
			} else if (fieldType == LudoFieldType.WIN) {
				return this.homeFields.get(pos.getFieldForType(type));
			}
			LOGGER.warn("Fail to get a default field with type {} at position {}", fieldType, pos.getGreen());
		}
		return this.fields.get(pos.getGreen());
	}
	
	public LudoField getField(LudoFigure figure) {
		List<LudoField> fields = Util.concatLists(this.fields, this.homeFields, this.winFields);
		for (LudoField field : fields) {
			if (!field.isEmpty() && field.getFigure().equals(figure)) {
				return field;
			}
		}
		LOGGER.warn("Fail to get field for figure {} of player {}", figure.getCount(), figure.getProfile().getName());
		return null;
	}
	
	@Nullable
	public LudoField getNextField(LudoFigure figure, int count) {
		LudoType type = figure.getType();
		LudoField field = this.getField(figure);
		if (field != null) {
			if (count > 0) {
				if (field.getType() == LudoFieldType.HOME && count == 6) {
					return this.getField(LudoFieldType.DEFAULT, null, LudoFieldPos.of(type, 0));
				} else {
					int position = field.getPos().getFieldForType(type) + count;
					if (position > 39) {
						if (position > 43) {
							LOGGER.info("The next field for figure {} of player {}, is out of map", figure.getCount(), figure.getProfile().getName());
							return null;
						}
						return this.getWinFields(type).get(position - 40);
					} else {
						return this.fields.get(position);
					}
				}
			}
			return field;
		}
		LOGGER.warn("Fail to get next field for figure {} of player {}, since the current field is null", figure.getCount(), figure.getProfile().getName());
		return null;
	}
	
	public List<LudoField> getHomeFields(LudoType type) {
		switch (type) {
			case GREEN: return this.homeFields.subList(0, 3);
			case YELLOW: return this.homeFields.subList(4, 7);
			case BLUE: return this.homeFields.subList(8, 11);
			case RED: return this.homeFields.subList(12, 15);
			default: break;
		}
		LOGGER.warn("Fail to get home fields for type {}", type);
		return Lists.newArrayList();
	}
	
	public List<LudoField> getWinFields(LudoType type) {
		switch (type) {
			case GREEN: return this.winFields.subList(0, 3);
			case YELLOW: return this.winFields.subList(4, 7);
			case BLUE: return this.winFields.subList(8, 11);
			case RED: return this.winFields.subList(12, 15);
			default: break;
		}
		LOGGER.warn("Fail to get win fields for type {}", type);
		return Lists.newArrayList();
	}
	
	public boolean canMoveFigure(LudoPlayer player, LudoFigure figure, int count) {
		if (count > 0) {
			LudoField nextField = this.getNextField(figure, count);
			if (nextField != null) {
				if (this.allFiguresAtHome(player)) {
					return count == 6 && nextField.isStart();
				} else {
					if (nextField.isEmpty()) {
						return true;
					} else {
						return nextField.getFigure().canKick(figure);
					}
				}

			}
		}
		return false;
	}
	
	public boolean canMoveAnyFigure(LudoPlayer player, int count) {
		for (LudoFigure figure : player.getFigures()) {
			if (this.canMoveFigure(player, figure, count)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean moveFigure(LudoFigure figure, int count) {
		LudoField currentField = this.getField(figure);
		LudoField nextField = this.getNextField(figure, count);
		if (currentField != null) {
			currentField.clearField();
		} else {
			LOGGER.warn("Fail to reset the current field of figure {} of player {}, since the current field is null", figure.getCount(), figure.getProfile().getName());
		}
		if (nextField != null) {
			LudoFigure opponentFigure = nextField.getFigure();
			if (nextField.isEmpty()) {
				nextField.setFigure(figure);
				return true;
			} else if (opponentFigure.canKick(figure)) {
				if (this.moveFigureHome(opponentFigure)) {
					nextField.setFigure(figure);
					return true;
				} else {
					LOGGER.warn("Fail to kick figure {} of player {} from field {}", opponentFigure.getCount(), opponentFigure.getProfile().getName(), nextField.getPos().getGreen());
				}
			} else {
				LOGGER.warn("Unable to move figure {} of player {} to a field {}, since there is already a figure of the player standing", figure.getCount(), figure.getProfile().getName(), nextField.getPos().getGreen());
			}
		}
		return false;
	}
	
	public boolean moveFigureHome(LudoFigure figure) {
		List<LudoField> fields = this.getHomeFields(figure.getType());
		for (LudoField field : fields) {
			if (field.isHome()) {
				if (field.isEmpty()) {
					field.setFigure(figure);
					return true;
				}
			} else {
				LOGGER.warn("Fail to move figure {} of player {} home, since the field at position {} is not a home field", figure.getCount(), figure.getProfile().getName(), field.getPos().getGreen());
			}
		}
		LOGGER.warn("Fail to move figure {} of player {} home, since there is no empty home field", figure.getCount(), figure.getProfile().getName());
		return false;
	}
	
	public boolean isFigurePlaying(LudoFigure figure) {
		LudoField field = this.getField(figure);
		if (field != null) {
			return !field.isHome();
		}
		return false;
	}
	
	public boolean allFiguresAtHome(LudoPlayer player) {
		boolean home = true;
		for (LudoFigure figure : player.getFigures()) {
			LudoField field = this.getField(figure);
			if (field != null && !field.isHome()) {
				home = false;
				break;
			}
		}
		return home;
	}
	
	public boolean hasFinished(LudoPlayer player) {
		boolean win = true;
		for (LudoFigure figure : player.getFigures()) {
			LudoField field = this.getField(figure);
			if (field != null && !field.isWin()) {
				win = false;
				break;
			}
		}
		return win;
	}
	
	public List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> getFigurePositions(List<LudoFigure> figures) {
		List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> figurePositions = Lists.newArrayList();
		for (LudoFigure figure : figures) {
			LudoField field = this.getField(figure);
			if (field != null) {
				figurePositions.add(new SimpleCell<>(field.getType(), field.getPos(), figure));
			} else {
				LOGGER.warn("Fail to sync figure {} of player {} to the client, since the figure has no field", figure.getCount(), figure.getProfile().getName());
			}
		}
		return figurePositions;
	}
	
	public void reset() {
		this.fields.forEach(LudoField::clearField);
		this.homeFields.forEach(LudoField::clearField);
		this.winFields.forEach(LudoField::clearField);
		LOGGER.info("Resetting ludo map");
	}
	
}
