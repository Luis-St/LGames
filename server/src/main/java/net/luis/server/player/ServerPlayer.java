package net.luis.server.player;

import javafx.scene.control.TreeItem;
import net.luis.language.TranslationKey;
import net.luis.game.score.PlayerScore;
import net.luis.network.Connection;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.server.Server;

/**
 *
 * @author Luis-st
 *
 */

public class ServerPlayer extends Player {
	
	public Connection connection;
	
	public ServerPlayer(GameProfile profile, PlayerScore score) {
		super(profile, score, Server.getInstance());
	}
	
	@Override
	public boolean isClient() {
		return false;
	}
	
	public TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.player", this.getProfile().getName()));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_name", this.getProfile().getName())));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_uuid", this.getProfile().getUUID())));
		String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_admin", ((Server) this.getApplication()).isAdmin(this) ? trueTranslation : falseTranslation)));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_playing", this.isPlaying() ? trueTranslation : falseTranslation)));
		return treeItem;
	}
	
	@Override
	public void setPlaying(boolean playing) {
		super.setPlaying(playing);
		((Server) this.getApplication()).refreshPlayers();
	}
	
}
