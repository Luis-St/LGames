package net.vgc.server.player;

import javafx.scene.control.TreeItem;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.dedicated.DedicatedServer;

public class ServerPlayer extends Player {
	
	protected final DedicatedServer server;
	public Connection connection;
	
	public ServerPlayer(GameProfile profile, DedicatedServer server) {
		super(profile);
		this.server = server;
	}
	
	@Override
	public boolean isClient() {
		return false;
	}
	
	public TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.player", this.profile.getName()));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_name", this.profile.getName())));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_uuid", this.profile.getUUID())));
		String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_admin", this.getServer().isAdmin(this) ? trueTranslation : falseTranslation)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.player_playing", this.isPlaying() ? trueTranslation : falseTranslation)));
		return treeItem;
	}
	
	@Override
	public void setPlaying(boolean playing) {
		super.setPlaying(playing);
		this.server.refreshPlayers();
	}
	
	public DedicatedServer getServer() {
		return this.server;
	}

}
