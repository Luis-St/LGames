package net.vgc.game.score;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.vgc.game.Game;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.annotation.DecodingConstructor;

public class GameScore implements Encodable {
	
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
	
	@DecodingConstructor
	private GameScore(FriendlyByteBuffer buffer) {
		this.scores = buffer.readMap(buffer, (buf) -> {
			return buf.read(GameProfile.class);
		}, (buf) -> {
			return buf.read(PlayerScore.class);
		});
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
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeMap(buffer, this.scores, (buf, key) -> {
			buf.write(key);
		}, (buf, value) -> {
			buf.write(value);
		});
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GameScore score) {
			return this.scores.equals(score.scores);
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("GameScore{");
		builder.append("scores=").append(this.scores).append("}");
		return builder.toString();
	}
	
}
