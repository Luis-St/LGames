package net.vgc.network.packet.client.game;

import net.luis.utils.util.ReflectionHelper;
import net.vgc.game.GameResult;
import net.vgc.game.win.GameResultLine;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.listener.PacketGetter;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class GameResultPacket implements ClientPacket {
	
	private final GameResult result;
	private final Encodable object;
	
	public GameResultPacket(GameResult result, Encodable object) {
		this.result = result;
		this.object = object;
	}
	
	@SuppressWarnings("unchecked")
	public GameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.readEnum(GameResult.class);
		boolean nullObject = buffer.readBoolean();
		if (!nullObject) {
			String clazz = buffer.readString();
			this.object = buffer.read((Class<Encodable>) ReflectionHelper.getClassForName(clazz));
		} else {
			this.object = null;
		}
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.result);
		buffer.writeBoolean(this.object == null);
		if (this.object != null) {
			buffer.writeString(this.object.getClass().getName());
			buffer.write(this.object);
		}
	}
	
	@PacketGetter
	public GameResult getResult() {
		return this.result;
	}
	
	@PacketGetter
	public Encodable getObject() {
		return this.object;
	}
	
	@Nullable
	@PacketGetter
	public GameResultLine getResultLine() {
		if (this.object instanceof GameResultLine resultLine) {
			return resultLine;
		}
		return null;
	}
	
}
