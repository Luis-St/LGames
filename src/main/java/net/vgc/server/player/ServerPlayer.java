package net.vgc.server.player;

import javafx.scene.control.TreeItem;
import net.vgc.language.TranslationKey;
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
	
	public TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.player", this.gameProfile.getName()));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_name", this.gameProfile.getName())));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_uuid", this.gameProfile.getUUID())));
		return treeItem;
	}

}
