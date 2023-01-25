package net.luis.game.player;

import net.luis.game.application.ApplicationType;
import net.luis.game.application.GameApplication;
import net.luis.game.player.score.PlayerScore;
import net.luis.network.Connection;
import net.luis.utility.Tickable;
import net.luis.utils.util.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Player implements Tickable {
	
	private final GameProfile profile;
	private final PlayerScore score;
	private final GameApplication application;
	private Connection connection;
	private boolean playing;
	
	public Player(GameProfile profile, PlayerScore score, GameApplication application) {
		this.profile = profile;
		this.score = score;
		this.application = application;
	}
	
	@Override
	public void tick() {
	
	}
	
	public abstract boolean isClient();
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public String getName() {
		return this.getProfile().getName();
	}
	
	public PlayerScore getScore() {
		return this.score;
	}
	
	public GameApplication getApplication() {
		return this.application;
	}
	
	public @Nullable Connection getConnection() {
		return ApplicationType.SERVER.isOn() ? this.connection : null;
	}
	
	public void setConnection(@Nullable Connection connection) {
		this.connection = ApplicationType.SERVER.isOn() ? connection : null;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player that)) return false;
		
		if (this.playing != that.playing) return false;
		if (!this.profile.equals(that.profile)) return false;
		if (!this.score.equals(that.score)) return false;
		return Objects.equals(this.connection, that.connection);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.profile, this.score, this.playing, this.connection);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "playing");
	}
}
