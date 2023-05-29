package net.luis.game.player;

import net.luis.game.Game;
import net.luis.game.application.GameApplication;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.score.PlayerScore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Player {
	
	private final GameApplication application;
	private final GameProfile profile;
	private final PlayerScore score;
	private boolean canSelect = false;
	
	public Player(GameApplication application, GameProfile profile, PlayerScore score) {
		this.application = Objects.requireNonNull(application, "Game application must not be null");
		this.profile = Objects.requireNonNull(profile, "Game profile must not be null");
		this.score = Objects.requireNonNull(score, "Player score must not be null");
	}
	
	public abstract boolean isClient();
	
	public @NotNull GameApplication getApplication() {
		return this.application;
	}
	
	public Game getGame() {
		return this.getApplication().getGameManager().getGameFor(this.getProfile());
	}
	
	public GamePlayer getPlayer() {
		return this.getGame() == null ? null : this.getGame().getPlayerFor(this);
	}
	
	public @NotNull GameProfile getProfile() {
		return this.profile;
	}
	
	public @NotNull String getName() {
		return this.getProfile().getName();
	}
	
	public @NotNull UUID getUniqueId() {
		return this.getProfile().getUniqueId();
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
		return this.getGame() != null && Objects.equals(this.getGame().getPlayer(), this.getPlayer());
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
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		
		if (this.canSelect != player.canSelect) return false;
		if (!this.application.equals(player.application)) return false;
		if (!this.profile.equals(player.profile)) return false;
		return this.score.equals(player.score);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.application, this.profile, this.score, this.canSelect);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + this.profile.getName();
	}
	//endregion
}
