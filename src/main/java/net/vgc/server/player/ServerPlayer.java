package net.vgc.server.player;

import javafx.scene.control.TreeItem;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.Server;

public class ServerPlayer extends Player {
	
	public Connection connection;
	
	public ServerPlayer(GameProfile gameProfile) {
		super(gameProfile);
	}
	
	@Override
	public boolean isClient() {
		return false;
	}
	
	public TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.player", this.gameProfile.getName()));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_name", this.gameProfile.getName())));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_uuid", this.gameProfile.getUUID())));
		String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_admin", Server.getInstance().getServer().isAdmin(this) ? trueTranslation : falseTranslation)));
		return treeItem;
	}

}
