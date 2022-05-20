package net.vgc.game.score;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

public class PlayerScore implements Encodable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final GameProfile profile;
	protected final MutableInt win;
	protected final MutableInt lose;
	protected final MutableInt draw;
	
	public PlayerScore(GameProfile profile) {
		this(profile, 0, 0, 0);
	}
	
	public PlayerScore(GameProfile profile, int win, int lose, int draw) {
		this.profile = profile;
		this.win = new MutableInt(win);
		this.lose = new MutableInt(lose);
		this.draw = new MutableInt(draw);
	}
	
	@DecodingConstructor
	private PlayerScore(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.win = new MutableInt(buffer.readInt());
		this.lose = new MutableInt(buffer.readInt());
		this.draw = new MutableInt(buffer.readInt());
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getWins() {
		return this.win.getValue();
	}
	
	public void setWins(int wins) {
		LOGGER.info("Update win value of player {} from {} to {}", this.profile.getName(), this.win.getValue(), wins);
		this.win.setValue(wins);
	}
	
	public void increaseWin() {
		LOGGER.info("Increase win value of player {} from {} to {}", this.profile.getName(), this.win.getValue(), this.win.getValue() + 1);
		this.win.increment();
	}
	
	public void resetWins() {
		this.setWins(0);
		LOGGER.info("Reset win value of player {} to {}", this.profile.getName(), 0);
	}
	
	public int getLoses() {
		return this.lose.getValue();	
	}
	
	public void setLoses(int loses) {
		LOGGER.info("Update lose value of player {} from {} to {}", this.profile.getName(), this.lose.getValue(), loses);
		this.lose.setValue(loses);
	}
	
	public void increaseLose() {
		LOGGER.info("Increase lose value of player {} from {} to {}", this.profile.getName(), this.lose.getValue(), this.lose.getValue() + 1);
		this.lose.increment();
	}
	
	public void resetLoses() {
		this.setLoses(0);
		LOGGER.info("Reset lose value of player {} to {}", this.profile.getName(), 0);
	}
	
	public int getDraws() {
		return this.draw.getValue();	
	}
	
	public void setDraws(int draws) {
		LOGGER.info("Update draw value of player {} from {} to {}", this.profile.getName(), this.draw.getValue(), draws);
		this.draw.setValue(draws);
	}
	
	public void increaseDraw() {
		LOGGER.info("Increase draw value of player {} from {} to {}", this.profile.getName(), this.draw.getValue(), this.draw.getValue() + 1);
		this.draw.increment();
	}
	
	public void resetDraws() {
		this.setDraws(0);
		LOGGER.info("Reset draw value of player {} to {}", this.profile.getName(), 0);
	}
	
	public void reset() {
		this.resetWins();
		this.resetLoses();
		this.resetDraws();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeInt(this.win.getValue());
		buffer.writeInt(this.lose.getValue());
		buffer.writeInt(this.draw.getValue());
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
			} else {
				return this.draw.equals(score.draw);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Score{");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("wins=").append(this.win.getValue()).append(",");
		builder.append("loses=").append(this.lose.getValue()).append(",");
		builder.append("draw=").append(this.draw.getValue()).append("}");
		return builder.toString();
	}
	
}
