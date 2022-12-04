package net.vgc.client.screen.update;

import org.jetbrains.annotations.Nullable;

import net.vgc.client.Client;
import net.vgc.client.fx.ScreenScene;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class ScreenUpdateFactory {
	
	private static void onUpdate(@Nullable String updateKey, @Nullable Object object) {
		if (Client.getInstance().getStage().getScene() instanceof ScreenScene scene) {
			scene.getScreen().onUpdate(updateKey, object);
		}
	}
	
	public static void onUpdate() {
		onUpdate(null, null);
	}
	
	public static void onPlayerUpdatePre(GameProfile profile) {
		onUpdate(ScreenUpdateKeys.PLAYER_UPDATE_PRE, profile);
	}
	
	public static void onPlayerUpdatePost(GameProfile profile) {
		onUpdate(ScreenUpdateKeys.PLAYER_UPDATE_POST, profile);
	}
	
	public static void onDiceUpdate(int count) {
		onUpdate(ScreenUpdateKeys.DICE_UPDATE, count);
	}
	
	public static void onGameResultUpdate() {
		onUpdate(ScreenUpdateKeys.GAME_RESULT_UPDATE, null);
	}
	
}
