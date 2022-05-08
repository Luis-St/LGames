package net.vgc.client.screen.game;

import net.vgc.client.player.LocalPlayer;
import net.vgc.client.screen.Screen;
import net.vgc.network.packet.client.ClientPacket;

public abstract class GameScreen extends Screen {
	
	protected boolean currentPlayer = false;
	
	public GameScreen() {
		
	}
	
	@Override
	public abstract void handlePacket(ClientPacket clientPacket);
	
	protected LocalPlayer getPlayer() {
		return this.client.getPlayer();
	}
	
}
