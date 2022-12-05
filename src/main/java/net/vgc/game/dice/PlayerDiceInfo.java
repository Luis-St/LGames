package net.vgc.game.dice;

import net.vgc.game.player.GamePlayer;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerDiceInfo {
	
	private final GamePlayer player;
	private final int count;
	
	public PlayerDiceInfo(GamePlayer player, int count) {
		this.player = player;
		this.count = count;
	}
	
	public GamePlayer getPlayer() {
		return this.player;
	}
	
	public int getCount() {
		return this.count;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof PlayerDiceInfo diceInfo) {
			if (!this.player.equals(diceInfo.player)) {
				return false;
			} else {
				return this.count == diceInfo.count;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PlayerDiceInfo{");
		builder.append("player=").append(this.player).append(",");
		builder.append("count=").append(this.count).append("}");
		return builder.toString();
	}
	
}
