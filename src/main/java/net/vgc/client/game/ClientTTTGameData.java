package net.vgc.client.game;

import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.ttt.TTTType;

public class ClientTTTGameData {
	
	protected final AbstractClientPlayer player;
	protected TTTType type = TTTType.NO;
	protected int wins = 0;
	protected int loses = 0;
	protected int draws = 0;
	
	public ClientTTTGameData(AbstractClientPlayer player) {
		this.player = player;
	}
	
	public AbstractClientPlayer getPlayer() {
		return this.player;
	}
	
	public TTTType getType() {
		return this.type;
	}
	
	public void setType(TTTType type) {
		this.type = type;
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
