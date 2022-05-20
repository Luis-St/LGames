package net.vgc.game.ttt.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.GameResult;
import net.vgc.game.ttt.TTTType;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

public class TTTMap implements Encodable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected TTTType topLeftType = TTTType.NO;
	protected TTTType topCenterType = TTTType.NO;
	protected TTTType topRightType = TTTType.NO;
	protected TTTType midLeftType = TTTType.NO;
	protected TTTType midCenterType = TTTType.NO;
	protected TTTType midRightType = TTTType.NO;
	protected TTTType bottomLeftType = TTTType.NO;
	protected TTTType bottomCenterType = TTTType.NO;
	protected TTTType bottomRightType = TTTType.NO;
	
	protected TTTMap() {
		
	}
	
	public TTTMap(TTTType topLeftType, TTTType topCenterType, TTTType topRightType, TTTType midLeftType, TTTType midCenterType, TTTType midRightType, TTTType bottomLeftType, TTTType bottomCenterType, TTTType bottomRightType) {
		this.topLeftType = topLeftType;
		this.topCenterType = topCenterType;
		this.topRightType = topRightType;
		this.midLeftType = midLeftType;
		this.midCenterType = midCenterType;
		this.midRightType = midRightType;
		this.bottomLeftType = bottomLeftType;
		this.bottomCenterType = bottomCenterType;
		this.bottomRightType = bottomRightType;
	}
	
	@DecodingConstructor
	private TTTMap(FriendlyByteBuffer buffer) {
		this.topLeftType = buffer.readEnum(TTTType.class);
		this.topCenterType = buffer.readEnum(TTTType.class);
		this.topRightType = buffer.readEnum(TTTType.class);
		this.midLeftType = buffer.readEnum(TTTType.class);
		this.midCenterType = buffer.readEnum(TTTType.class);
		this.midRightType = buffer.readEnum(TTTType.class);
		this.bottomLeftType = buffer.readEnum(TTTType.class);
		this.bottomCenterType = buffer.readEnum(TTTType.class);
		this.bottomRightType = buffer.readEnum(TTTType.class);
	}
	
	public TTTType getTopLeftType() {
		return this.topLeftType;
	}
	
	public TTTType getTopCenterType() {
		return this.topCenterType;
	}
	
	public TTTType getTopRightType() {
		return this.topRightType;
	}
	
	public TTTType getMidLeftType() {
		return this.midLeftType;
	}
	
	public TTTType getMidCenterType() {
		return this.midCenterType;
	}
	
	public TTTType getMidRightType() {
		return this.midRightType;
	}
	
	public TTTType getBottomLeftType() {
		return this.bottomLeftType;
	}
	
	public TTTType getBottomCenterType() {
		return this.bottomCenterType;
	}
	
	public TTTType getBottomRightType() {
		return this.bottomRightType;
	}
	
	public TTTType getType(int vMap, int hMap) {
		if (vMap == 0) {
			if (hMap == 0) {
				return this.topLeftType;
			} else if (hMap == 1) {
				return this.midLeftType;
			} else if (hMap == 2) {
				return this.bottomLeftType;
			}
		} else if (vMap == 1) {
			if (hMap == 0) {
				return this.topCenterType;
			} else if (hMap == 1) {
				return this.midCenterType;
			} else if (hMap == 2) {
				return this.bottomCenterType;
			}
		} else if (vMap == 2) {
			if (hMap == 0) {
				return this.topRightType;
			} else if (hMap == 1) {
				return this.midRightType;
			} else if (hMap == 2) {
				return this.bottomRightType;
			}
		}
		LOGGER.warn("Fail to get field type in map at pos {}:{}", vMap, hMap);
		return TTTType.NO;
	}
	

	
	public TTTResultLine getResultLine() {
		TTTResultLine resultLine = TTTResultLine.EMPTY;
		for (int v = 0; v < 3; v++) {
			resultLine = this.getLine(v, 0, v, 1, v, 2);
			if (!resultLine.equals(TTTResultLine.EMPTY)) {
				return resultLine;
			}
		}
		for (int h = 0; h < 3; h++) {
			resultLine = this.getLine(0, h, 1, h, 2, h);
			if (!resultLine.equals(TTTResultLine.EMPTY)) {
				return resultLine;
			}
		}
		resultLine = this.getLine(0, 0, 1, 1, 2, 2);
		if (resultLine.equals(TTTResultLine.EMPTY)) {
			resultLine = this.getLine(2, 0, 1, 1, 0, 2);
		}
		return resultLine;
	}
	
	protected TTTResultLine getLine(int vMap0, int hMap0, int vMap1, int hMap1, int vMap2, int hMap2) {
		TTTResultLine resultLine = TTTResultLine.EMPTY;
		TTTType type = this.getType(vMap0, hMap0);
		if (type != TTTType.NO) {
			if (type == this.getType(vMap1, hMap1) && type == this.getType(vMap2, hMap2)) {
				resultLine = new TTTResultLine(type, vMap0, hMap0, vMap1, hMap1, vMap2, hMap2);
			}
		}
		return resultLine;
	}
	
	public boolean hasSpace() {
		for (int v = 0; v < 3; v++) {
			for (int h = 0; h < 3; h++) {
				if (this.getType(v, h) == TTTType.NO) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isDraw() {
		return !this.hasSpace() && this.getResultLine().equals(TTTResultLine.EMPTY);
	}
	
	public boolean hasResult() {
		return !this.getResultLine().equals(TTTResultLine.EMPTY) || this.isDraw();
	}
	
	public GameResult getResult(TTTType type) {
		if (this.hasResult()) {
			if (this.isDraw()) {
				return GameResult.DRAW;
			}
			return this.getWinner() == type ? GameResult.WIN : GameResult.LOSE;
		}
		return GameResult.NO;
	}
	
	public TTTType getWinner() {
		return this.getResultLine().getType();
	}
	
	public TTTType getLoser() {
		return this.getWinner().getOpponents().get(0);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.topLeftType);
		buffer.writeEnum(this.topCenterType);
		buffer.writeEnum(this.topRightType);
		buffer.writeEnum(this.midLeftType);
		buffer.writeEnum(this.midCenterType);
		buffer.writeEnum(this.midRightType);
		buffer.writeEnum(this.bottomLeftType);
		buffer.writeEnum(this.bottomCenterType);
		buffer.writeEnum(this.bottomRightType);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTMap map) {
			if (!this.topLeftType.equals(map.topLeftType)) {
				return false;
			} else if (!this.topCenterType.equals(map.topCenterType)) {
				return false;
			} else if (!this.topRightType.equals(map.topRightType)) {
				return false;
			} else if (!this.midLeftType.equals(map.midLeftType)) {
				return false;
			} else if (!this.midCenterType.equals(map.midCenterType)) {
				return false;
			} else if (!this.midRightType.equals(map.midRightType)) {
				return false;
			} else if (!this.bottomLeftType.equals(map.bottomLeftType)) {
				return false;
			} else if (!this.bottomCenterType.equals(map.bottomCenterType)) {
				return false;
			} else {
				return this.bottomRightType.equals(map.bottomRightType);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTMap{");
		builder.append("topLeft=").append(this.topLeftType).append(",");
		builder.append("topCenter=").append(this.topCenterType).append(",");
		builder.append("topRight=").append(this.topRightType).append(",");
		builder.append("midLeft=").append(this.midLeftType).append(",");
		builder.append("midCenter=").append(this.midCenterType).append(",");
		builder.append("midRight=").append(this.midRightType).append(",");
		builder.append("bottompLeft=").append(this.bottomLeftType).append(",");
		builder.append("bottomCenter=").append(this.bottomCenterType).append(",");
		builder.append("bottomRight=").append(this.bottomRightType).append("}");
		return builder.toString();
	}
	
}
