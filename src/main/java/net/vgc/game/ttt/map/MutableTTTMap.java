package net.vgc.game.ttt.map;

import net.vgc.game.ttt.TTTType;

public class MutableTTTMap extends TTTMap {
	
	public MutableTTTMap() {
		super();
	}
	
	public MutableTTTMap(TTTType topLeftType, TTTType topCenterType, TTTType topRightType, TTTType midLeftType, TTTType midCenterType, TTTType midRightType, TTTType bottomLeftType, TTTType bottomCenterType, TTTType bottomRightType) {
		super(topLeftType, topCenterType, topRightType, midLeftType, midCenterType, midRightType, bottomLeftType, bottomCenterType, bottomRightType);
	}
	
	public void setTopLeftType(TTTType topLeftType) {
		this.topLeftType = topLeftType;
	}
	
	public void setTopCenterType(TTTType topCenterType) {
		this.topCenterType = topCenterType;
	}
	
	public void setTopRightType(TTTType topRightType) {
		this.topRightType = topRightType;
	}
	
	public void setMidLeftType(TTTType midLeftType) {
		this.midLeftType = midLeftType;
	}
	
	public void setMidCenterType(TTTType midCenterType) {
		this.midCenterType = midCenterType;
	}
	
	public void setMidRightType(TTTType midRightType) {
		this.midRightType = midRightType;
	}
	
	public void setBottomLeftType(TTTType bottomLeftType) {
		this.bottomLeftType = bottomLeftType;
	}
	
	public void setBottomCenterType(TTTType bottomCenterType) {
		this.bottomCenterType = bottomCenterType;
	}
	
	public void setBottomRightType(TTTType bottomRightType) {
		this.bottomRightType = bottomRightType;
	}
	
	public void setType(TTTType type, int vGrid, int hGrid) {
		if (vGrid == 0) {
			if (hGrid == 0) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.topLeftType, type);
				this.topLeftType = type;
			} else if (hGrid == 1) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.midLeftType, type);
				this.midLeftType = type;
			} else if (hGrid == 2) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.bottomLeftType, type);
				this.bottomLeftType = type;
			} else {
				LOGGER.warn("Fail to set type for field at map pos {}:{}", vGrid, hGrid);
			}
		} else if (vGrid == 1) {
			if (hGrid == 0) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.topCenterType, type);
				this.topCenterType = type;
			} else if (hGrid == 1) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.midCenterType, type);
				this.midCenterType = type;
			} else if (hGrid == 2) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.bottomCenterType, type);
				this.bottomCenterType = type;
			} else {
				LOGGER.warn("Fail to set type for field at map pos {}:{}", vGrid, hGrid);
			}
		} else if (vGrid == 2) {
			if (hGrid == 0) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.topRightType, type);
				this.topRightType = type;
			} else if (hGrid == 1) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.midRightType, type);
				this.midRightType = type;
			} else if (hGrid == 2) {
				LOGGER.debug("Update field at map pos {}:{} from {} to {}", vGrid, hGrid, this.bottomRightType, type);
				this.bottomRightType = type;
			} else {
				LOGGER.warn("Fail to set type for field at map pos {}:{}", vGrid, hGrid);
			}
		} else {
			LOGGER.warn("Fail to set type for field at map pos {}:{}", vGrid, hGrid);
		}
	}
	
	public TTTMap immutable() {
		return new TTTMap(this.topLeftType, this.topCenterType, this.topRightType, this.midLeftType, this.midCenterType, this.midRightType, this.bottomLeftType, this.bottomCenterType, this.bottomRightType);
	}
	
}
