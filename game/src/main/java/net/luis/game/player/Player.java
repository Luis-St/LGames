package net.luis.game.player;

import net.luis.game.Game;
import net.luis.game.application.GameApplication;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.score.PlayerScore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Player {
	
	private final GameApplication application;
	private final GameProfile profile;
	private final Connection connection;
	private final PlayerScore score;
	private boolean canSelect = false;
	
	public Player(@NotNull GameApplication application, @NotNull GameProfile profile, @Nullable Connection connection, @NotNull PlayerScore score) {
		this.application = application;
		this.profile = profile;
		this.connection = connection;
		this.score = score;
	}
	
	public abstract boolean isClient();
	
	public @NotNull GameApplication getApplication() {
		return this.application;
	}
	
	public Game getGame() {
		return this.getApplication().getGameManager().getGameFor(this.getProfile());
	}
	
	public GamePlayer getPlayer() {
		if (this.getGame() == null) {
			return null;
		}
		return this.getGame().getPlayerFor(this);
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
		return false;
	}
	
	public boolean isPlaying() {
		return this.getApplication().getGameManager().getGameFor(this.getProfile()) != null;
	}
	
	public boolean isCurrent() {
		if (this.getGame() == null) {
			return false;
		}
		return Objects.equals(this.getGame().getPlayer(), this.getPlayer());
	}
	
	public boolean canSelect() {
		return this.canSelect;
	}
	
	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}
	
	public boolean canRollDice() {
		if (this.getGame() == null || !this.getGame().isDiceGame()) {
			return false;
		}
		return Objects.requireNonNull(this.getGame().getDiceHandler()).canRoll(Objects.requireNonNull(this.getPlayer()));
	}
	
	public int getDiceCount() {
		if (this.getGame() == null || !this.getGame().isDiceGame()) {
			return -1;
		}
		return Objects.requireNonNull(this.getGame().getDiceHandler()).getLastCount(Objects.requireNonNull(this.getPlayer()));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		
		if (this.canSelect != player.canSelect) return false;
		if (!this.application.equals(player.application)) return false;
		if (!this.profile.equals(player.profile)) return false;
		if (!Objects.equals(this.connection, player.connection)) return false;
		return this.score.equals(player.score);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.application, this.profile, this.connection, this.score, this.canSelect);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "application", "connection");
	}
}
