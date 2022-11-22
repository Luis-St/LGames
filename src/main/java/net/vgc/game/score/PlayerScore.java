package net.vgc.game.score;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

public class PlayerScore implements Encodable {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GameProfile profile;
	private final MutableInt win;
	private final MutableInt lose;
	private final MutableInt draw;
	private final MutableInt score;
	
	public PlayerScore(GameProfile profile) {
		this(profile, 0, 0, 0, 0);
	}
	
	public PlayerScore(GameProfile profile, int win, int lose, int draw, int score) {
		this.profile = profile;
		this.win = new MutableInt(win);
		this.lose = new MutableInt(lose);
		this.draw = new MutableInt(draw);
		this.score = new MutableInt(score);
	}
	
	@DecodingConstructor
	private PlayerScore(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.win = new MutableInt(buffer.readInt());
		this.lose = new MutableInt(buffer.readInt());
		this.draw = new MutableInt(buffer.readInt());
		this.score = new MutableInt(buffer.readInt());
	}
	
	public void sync(PlayerScore score) {
		if (this.profile.equals(score.profile)) {
			this.win.setValue(score.getWins());
			this.lose.setValue(score.getLoses());
			this.draw.setValue(score.getDraws());
			this.score.setValue(score.getScore());
		} else {
			LOGGER.warn("Fail to sync player score of player {} with player {}", this.profile, score.profile);
		}
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getWins() {
		return this.win.getValue();
	}
	
	public void setWins(int wins) {
		this.win.setValue(wins);
	}
	
	public void increaseWin() {
		this.win.increment();
	}
	
	public void resetWins() {
		this.setWins(0);
	}
	
	public int getLoses() {
		return this.lose.getValue();
	}
	
	public void setLoses(int loses) {
		this.lose.setValue(loses);
	}
	
	public void increaseLose() {
		this.lose.increment();
	}
	
	public void resetLoses() {
		this.setLoses(0);
	}
	
	public int getDraws() {
		return this.draw.getValue();
	}
	
	public void setDraws(int draws) {
		this.draw.setValue(draws);
	}
	
	public void increaseDraw() {
		this.draw.increment();
	}
	
	public void resetDraws() {
		this.setDraws(0);
	}
	
	public int getScore() {
		return this.score.getValue();
	}
	
	public void setScore(int score) {
		this.score.setValue(score);
	}
	
	public void increaseScore() {
		this.score.increment();
	}
	
	public void resetScore() {
		this.setScore(0);
	}
	
	public void reset() {
		this.resetWins();
		this.resetLoses();
		this.resetDraws();
		this.resetScore();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeInt(this.win.getValue());
		buffer.writeInt(this.lose.getValue());
		buffer.writeInt(this.draw.getValue());
		buffer.writeInt(this.score.getValue());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof PlayerScore score) {
			if (!this.profile.equals(score.profile)) {
				return false;
			} else if (!this.win.equals(score.win)) {
				return false;
			} else if (!this.lose.equals(score.lose)) {
				return false;
			} else if (!this.draw.equals(score.draw)) {
				return false;
			} else {
				return this.score.equals(score.score);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PlayerScore{");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("wins=").append(this.win.getValue()).append(",");
		builder.append("loses=").append(this.lose.getValue()).append(",");
		builder.append("draw=").append(this.draw.getValue()).append(",");
		builder.append("score=").append(this.score.getValue()).append("}");
		return builder.toString();
	}
	
}
