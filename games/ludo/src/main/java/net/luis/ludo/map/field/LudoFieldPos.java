package net.luis.ludo.map.field;

import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayerType;
import net.luis.ludo.LudoGame;
import net.luis.ludo.player.LudoPlayerType;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class LudoFieldPos implements GameFieldPos {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoGame.class);
	
	private final int green;
	private final int yellow;
	private final int blue;
	private final int red;
	
	public LudoFieldPos(int green, int yellow, int blue, int red) {
		this.green = green;
		this.yellow = yellow;
		this.blue = blue;
		this.red = red;
	}
	
	@DecodingConstructor
	public LudoFieldPos(FriendlyByteBuffer buffer) {
		this.green = buffer.readInt();
		this.yellow = buffer.readInt();
		this.blue = buffer.readInt();
		this.red = buffer.readInt();
	}
	
	public static @NotNull LudoFieldPos of(int pos) {
		return new LudoFieldPos(pos, pos, pos, pos);
	}
	
	public static @Nullable LudoFieldPos of(GamePlayerType playerType, int pos) {
		if (playerType instanceof LudoPlayerType) {
			return of((LudoPlayerType) playerType, pos);
		}
		throw new ClassCastException("Can not cast playerType of type " + playerType.getClass().getSimpleName() + " to LudoPlayerType");
	}
	
	private static @Nullable LudoFieldPos of(LudoPlayerType playerType, int pos) {
		return switch (playerType) {
			case GREEN -> ofGreen(pos);
			case YELLOW -> ofYellow(pos);
			case BLUE -> ofBlue(pos);
			case RED -> ofRed(pos);
			default -> {
				LOGGER.warn("Fail to create field pos for ludo type {}", playerType);
				yield null;
			}
		};
	}
	
	public static @NotNull LudoFieldPos ofGreen(int green) {
		return new LudoFieldPos(green, (green + 30) % 40, (green + 20) % 40, (green + 10) % 40);
	}
	
	public static @NotNull LudoFieldPos ofYellow(int yellow) {
		return new LudoFieldPos((yellow + 10) % 40, yellow, (yellow + 30) % 40, (yellow + 20) % 40);
	}
	
	public static @NotNull LudoFieldPos ofBlue(int blue) {
		return new LudoFieldPos((blue + 20) % 40, (blue + 10) % 40, blue, (blue + 30) % 40);
	}
	
	public static @NotNull LudoFieldPos ofRed(int red) {
		return new LudoFieldPos((red + 30) % 40, (red + 20) % 40, (red + 10) % 40, red);
	}
	
	@Override
	public int getPosition() {
		return this.green;
	}
	
	public int getGreen() {
		return this.green;
	}
	
	public int getYellow() {
		return this.yellow;
	}
	
	public int getBlue() {
		return this.blue;
	}
	
	public int getRed() {
		return this.red;
	}
	
	@Override
	public int getPositionFor(@NotNull GamePlayerType playerType) {
		if (playerType instanceof LudoPlayerType) {
			return switch ((LudoPlayerType) playerType) {
				case GREEN -> this.green;
				case YELLOW -> this.yellow;
				case BLUE -> this.blue;
				case RED -> this.red;
				default -> this.green;
			};
		}
		throw new ClassCastException("Can not cast playerType of type " + playerType.getClass().getSimpleName() + " to LudoPlayerType");
	}
	
	@Override
	public boolean isStart() {
		if (this.green == 0 && this.yellow == 30 && this.blue == 20 && this.red == 10) {
			return true;
		} else if (this.green == 10 && this.yellow == 0 && this.blue == 30 && this.red == 20) {
			return true;
		} else if (this.green == 20 && this.yellow == 10 && this.blue == 0 && this.red == 30) {
			return true;
		} else return this.green == 30 && this.yellow == 20 && this.blue == 10 && this.red == 0;
	}
	
	@Override
	public boolean isOutOfMap() {
		return !Mth.isInBounds(this.green, 0, 39) || !Mth.isInBounds(this.yellow, 0, 39) || !Mth.isInBounds(this.blue, 0, 39) || !Mth.isInBounds(this.red, 0, 39);
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeInt(this.green);
		buffer.writeInt(this.yellow);
		buffer.writeInt(this.blue);
		buffer.writeInt(this.red);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LudoFieldPos that)) return false;
		
		if (this.green != that.green) return false;
		if (this.yellow != that.yellow) return false;
		if (this.blue != that.blue) return false;
		return this.red == that.red;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.green, this.yellow, this.blue, this.red);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
