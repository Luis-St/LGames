package net.vgc.game.score;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.vgc.game.Game;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class GameScore {
	
	protected final Map<GameProfile, PlayerScore> scores;
	
	public GameScore(Game game) {
		this(game.getPlayers());
	}
	
	public GameScore(List<ServerPlayer> players) {
		this(createScoreMap(players));
	}
	
	public GameScore(Map<GameProfile, PlayerScore> scores) {
		this.scores = scores;
	}
	
	protected static Map<GameProfile, PlayerScore> createScoreMap(List<ServerPlayer> players) {
		Map<GameProfile, PlayerScore> playerScores = Maps.newHashMap();
		for (ServerPlayer player : players) {
			playerScores.put(player.getProfile(), new PlayerScore(player.getProfile()));
		}
		return playerScores;
	}
	
	public Map<GameProfile, PlayerScore> getScores() {
		return this.scores;
	}
	
	public boolean hasScore(GameProfile profile) {
		return this.scores.containsKey(profile);
	}
	
	public PlayerScore getScore(GameProfile profile) {
		return this.scores.get(profile);
	}
	
	public void setScore(GameProfile profile, PlayerScore score) {
		if (this.hasScore(profile)) {
			this.removeScore(profile);
		}
		this.addScore(profile, score);
	}
	
	public void addScore(GameProfile profile, PlayerScore score) {
		if (!this.hasScore(profile)) {
			this.scores.put(profile, score);
		}
	}
	
	public void removeScore(GameProfile profile) {
		if (this.hasScore(profile)) {
			this.scores.remove(profile);
		}
	}
	
	public void resetScore(GameProfile profile) {
		this.getScore(profile).reset();
	}
	
	public void resetScores() {
		for (GameProfile profile : this.scores.keySet()) {
			this.resetScore(profile);
		}
	}
	
}
