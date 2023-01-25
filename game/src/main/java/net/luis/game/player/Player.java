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
	private boolean playing = false;
	private boolean current = false;
	private boolean canSelect = false;
	private boolean canRollDice = false;
	private int count = -1;
	
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
		
		if (this.playing != player.playing) return false;
		if (this.current != player.current) return false;
		if (this.canSelect != player.canSelect) return false;
		if (this.canRollDice != player.canRollDice) return false;
		if (this.count != player.count) return false;
		if (!this.profile.equals(player.profile)) return false;
		if (!this.score.equals(player.score)) return false;
		return Objects.equals(this.connection, player.connection);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.profile, this.score, this.connection, this.playing, this.current, this.canSelect, this.canRollDice, this.count);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "playing");
	}
}
