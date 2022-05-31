package net.vgc.game.player;

import java.util.List;

import net.vgc.Main;
import net.vgc.language.TranslationKey;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.EnumRepresentable;

public interface GamePlayerType extends EnumRepresentable, Encodable {
	
	TranslationKey getTranslation();
	
	List<? extends GamePlayerType> getOpponents();
	
	@Override
	default void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.getClass().getName());
		buffer.writeInt(this.getId());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & EnumRepresentable> T decode(FriendlyByteBuffer buffer) {
		String className = buffer.readString();
		int id = buffer.readInt();
		try {
			return EnumRepresentable.fromId((Class<T>) Class.forName(className), id);
		} catch (Exception e) {
			Main.LOGGER.warn("Fail to decode game player type with id {} of type {}", className, id);
		}
		return null;
	}
	
}
