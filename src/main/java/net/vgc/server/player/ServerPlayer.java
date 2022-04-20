package net.vgc.server.player;

import javafx.scene.Group;
import javafx.scene.Node;
import net.vgc.network.Connection;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public class ServerPlayer extends Player {
	
	public Connection connection;
	
	public ServerPlayer(GameProfile gameProfile) {
		super(gameProfile);
	}
	
	@Override
	public void tick() {
		
	}
	
	public Node display() {
		return new Group();
	}

}
