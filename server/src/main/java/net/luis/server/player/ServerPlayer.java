package net.luis.server.player;

import javafx.scene.control.TreeItem;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.score.PlayerScore;
import net.luis.language.TranslationKey;
import net.luis.network.Connection;
import net.luis.server.Server;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class ServerPlayer extends Player {
	
	public ServerPlayer(@NotNull GameProfile profile, @NotNull Connection connection) {
		super(Server.getInstance(), profile, connection, new PlayerScore(profile));
	}
	
	@Override
	public boolean isClient() {
		return false;
	}
	
	public @NotNull TreeItem<String> display() {
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.player", this.getProfile().getName()));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_name", this.getProfile().getName())));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_uuid", this.getProfile().getUniqueId())));
		String trueTranslation = TranslationKey.createAndGet("window.create_account.true");
		String falseTranslation = TranslationKey.createAndGet("window.create_account.false");
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_admin", this.isAdmin() ? trueTranslation : falseTranslation)));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.player_playing", this.isPlaying() ? trueTranslation : falseTranslation)));
		return treeItem;
	}
	
	@Override
	public void setPlaying(boolean playing) {
		super.setPlaying(playing);
		this.getApplication().getScreen().refresh();
	}
	
}
