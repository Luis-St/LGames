package net.vgc.client.game;

import net.vgc.Main;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.GameTypes;
import net.vgc.game.ttt.TTTType;

public class ClientTTTGameData {
	
	protected AbstractClientPlayer player = null;
	protected TTTType type = TTTType.NO;
	protected boolean current = false;
	protected int wins = 0;
	protected int loses = 0;
	protected int draws = 0;
	
	public AbstractClientPlayer getPlayer() {
		return this.player;
	}
	
	public void setPlayer(AbstractClientPlayer player) {
		if (this.player == null) {
			this.player = player;
		} else {
			Main.LOGGER.warn("Fail to set player of client {} game data to {}, since the player is already set to {}", GameTypes.TIC_TAC_TOE.getName().toLowerCase(), player.getProfile().getName(), this.player.getProfile().getName());
		}
	}
	
	public TTTType getType() {
		return this.type;
	}
	
	public void setType(TTTType type) {
		if (this.type == TTTType.NO) {
			this.type = type;
		} else {
			Main.LOGGER.warn("Fail to set player type of client {} game data to {}, since the type is already set to {}", GameTypes.TIC_TAC_TOE.getName().toLowerCase(), type, this.type);
		}
	}
	
	public boolean isCurrent() {
		return this.current;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public int getLoses() {
		return this.loses;
	}
	
	public void setLoses(int loses) {
		this.loses = loses;
	}
	
	public int getDraws() {
		return this.draws;
	}
	
	public void setDraws(int draws) {
		this.draws = draws;
	}
	
	public boolean isRequiredDataLoad() {
		return this.player != null && this.type != TTTType.NO;
	}
	
}
