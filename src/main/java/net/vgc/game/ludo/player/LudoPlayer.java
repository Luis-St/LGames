package net.vgc.game.ludo.player;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.map.LudoMap;
import net.vgc.game.ludo.map.field.LudoField;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.exception.InvalidValueException;

public class LudoPlayer {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final ServerPlayer player;
	protected final LudoType type;
	protected final List<LudoFigure> figures;
	
	public LudoPlayer(ServerPlayer player, LudoType type, int figureCount) {
		this.player = player;
		this.type = type;
		this.figures = createFigures(this.player, this.type, figureCount);
	}
	
	protected static List<LudoFigure> createFigures(ServerPlayer player, LudoType type, int figureCount) {
		List<LudoFigure> figures = Lists.newArrayList();
		if (!Mth.isInBounds(figureCount, 1, 4)) {
			LOGGER.error("Fail to create figure list for count {}, since the count is out of bound 1 - 4", figureCount);
			throw new InvalidValueException("Fail to create figure list for count " + figureCount + "{}, since the count is out of bound 1 - 4");
		}
		for (int i = 0; i < figureCount; i++) {
			figures.add(new LudoFigure(player.getProfile(), i, type));
		}
		return figures;
	}
	
	public ServerPlayer getPlayer() {
		return this.player;
	}
	
	public LudoType getType() {
		return this.type;
	}
	
	public int getFigureCount() {
		return this.figures.size();
	}
	
	public List<LudoFigure> getFigures() {
		return this.figures;
	}
	
	@Nullable
	public LudoFigure getFigure(int figure) {
		int count = this.getFigureCount() - 1;
		if (figure > count || 0 > figure) {
			LOGGER.warn("Fail to get figure with index {}, since the index is out of bounds 0 - {}", figure, count);
			return null;
		}
		return this.figures.get(figure);
	}
	
	@Nullable
	public LudoFigure getFigureFromField(LudoMap map, LudoFieldPos pos) {
		for (LudoFigure figure : this.figures) {
			LudoField field = map.getField(figure);
			if (field != null) {
				if (field.getPos().equals(pos)) {
					return figure;
				}
			} else {
				LOGGER.warn("Fail to get field for figure {} of player {}", figure.getCount(), this.player.getProfile().getName());
			}
		}
		return null;
	}
	
	@Nullable
	public LudoFigure getUnusedFigure(LudoMap map) {
		if (!this.areAllFiguresPlaying(map)) {
			for (LudoFigure figure : this.figures) {
				if (!map.isFigurePlaying(figure)) {
					return figure;
				}
			}
		}
		return null;
	}
	
	public boolean areAllFiguresPlaying(LudoMap map) {
		return this.figures.stream().filter((figure) -> {
			return !map.isFigurePlaying(figure);
		}).findAny().isEmpty();
	}
	
	public boolean hasFigureAtStart(LudoMap map) {
		for (LudoFigure figure : this.figures) {
			LudoField field = map.getField(figure);
			if (field != null) {
				if (field.isStart()) {
					return true;
				}
			} else {
				LOGGER.warn("Fail to get field for figure {} of player {}", figure.getCount(), this.player.getProfile().getName());
			}
		}
		return false;
	}
	
	public boolean hasFigureAtField(LudoMap map, LudoFieldPos pos) {
		return this.getFigureFromField(map, pos) != null;
	}

}
