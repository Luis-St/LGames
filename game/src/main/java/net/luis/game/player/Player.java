package net.luis.game.player;

import net.luis.game.application.FxApplication;
import net.luis.game.player.score.PlayerScore;
import net.luis.network.Connection;
import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Player {
	
	private final FxApplication application;
	private final GameProfile profile;
	private final Connection connection;
	private final PlayerScore score;
	private boolean admin = false;
	private boolean playing = false;
	private boolean current = false;
	private boolean canSelect = false;
	private boolean canRollDice = false;
	private int count = -1;
	
	public Player(@NotNull FxApplication application, @NotNull GameProfile profile, @Nullable Connection connection, @NotNull PlayerScore score) {
		this.application = application;
		this.profile = profile;
		this.connection = connection;
		this.score = score;
	}
	
	public abstract boolean isClient();
	
	public @NotNull FxApplication getApplication() {
		return this.application;
	}
	
	public @NotNull GameProfile getProfile() {
		return this.profile;
	}
	
	public @NotNull String getName() {
		return this.getProfile().getName();
	}
	
	public @NotNull Connection getConnection() {
		return Objects.requireNonNull(this.connection);
	}
	
	public @NotNull PlayerScore getScore() {
		return this.score;
	}
	
	public boolean isAdmin() {
		return this.admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	public boolean isCurrent() {
		return this.current;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public boolean canSelect() {
		return this.canSelect;
	}
	
	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}
	
	public boolean canRollDice() {
		return this.canRollDice;
	}
	
	public void setCanRollDice(boolean canRollDice) {
		this.canRollDice = canRollDice;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		
		if (this.admin != player.admin) return false;
		if (this.playing != player.playing) return false;
		if (this.current != player.current) return false;
		if (this.canSelect != player.canSelect) return false;
		if (this.canRollDice != player.canRollDice) return false;
		if (this.count != player.count) return false;
		if (!this.application.equals(player.application)) return false;
		if (!this.profile.equals(player.profile)) return false;
		if (!this.connection.equals(player.connection)) return false;
		return this.score.equals(player.score);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.application, this.profile, this.connection, this.score, this.admin, this.playing, this.current, this.canSelect, this.canRollDice, this.count);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this, "application", "connection");
	}
}
