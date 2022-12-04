package net.vgc.games.ludo.map.field;

import org.jetbrains.annotations.Nullable;

import net.luis.utils.math.Mth;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

public class LudoFieldPos implements GameFieldPos {
	
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
	
	public static LudoFieldPos of(int pos) {
		return new LudoFieldPos(pos, pos, pos, pos);
	}
	
	@Nullable
	public static LudoFieldPos of(GamePlayerType playerType, int pos) {
		if (playerType instanceof LudoPlayerType) {
			return of((LudoPlayerType) playerType, pos);
		}
		throw new ClassCastException("Can not cast playerType of type " + playerType.getClass().getSimpleName() + " to LudoPlayerType");
	}
	
	@Nullable
	private static LudoFieldPos of(LudoPlayerType playerType, int pos) {
		switch (playerType) {
			case GREEN:
				return ofGreen(pos);
			case YELLOW:
				return ofYellow(pos);
			case BLUE:
				return ofBlue(pos);
			case RED:
				return ofRed(pos);
			default:
				break;
		}
		LOGGER.warn("Fail to create field pos for ludo type {}", playerType);
		return null;
	}
	
	public static LudoFieldPos ofGreen(int green) {
		return new LudoFieldPos(green, (green + 30) % 40, (green + 20) % 40, (green + 10) % 40);
	}
	
	public static LudoFieldPos ofYellow(int yellow) {
		return new LudoFieldPos((yellow + 10) % 40, yellow, (yellow + 30) % 40, (yellow + 20) % 40);
	}
	
	public static LudoFieldPos ofBlue(int blue) {
		return new LudoFieldPos((blue + 20) % 40, (blue + 10) % 40, blue, (blue + 30) % 40);
	}
	
	public static LudoFieldPos ofRed(int red) {
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
	public int getPositionFor(GamePlayerType playerType) {
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
		} else if (this.green == 30 && this.yellow == 20 && this.blue == 10 && this.red == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isOutOfMap() {
		if (Mth.isInBounds(this.green, 0, 39) && Mth.isInBounds(this.yellow, 0, 39) && Mth.isInBounds(this.blue, 0, 39) && Mth.isInBounds(this.red, 0, 39)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.green);
		buffer.writeInt(this.yellow);
		buffer.writeInt(this.blue);
		buffer.writeInt(this.red);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoFieldPos pos) {
			if (this.green != pos.green) {
				return false;
			} else if (this.yellow != pos.yellow) {
				return false;
			} else if (this.blue != pos.blue) {
				return false;
			} else {
				return this.red == pos.red;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoFieldPos{");
		builder.append("green=").append(this.green).append(",");
		builder.append("yellow=").append(this.yellow).append(",");
		builder.append("blue=").append(this.blue).append(",");
		builder.append("red=").append(this.red).append("}");
		return builder.toString();
	}
	
}
