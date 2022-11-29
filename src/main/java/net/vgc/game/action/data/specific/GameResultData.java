package net.vgc.game.action.data.specific;

import net.luis.utils.util.ReflectionHelper;
import net.vgc.game.GameResult;
import net.vgc.game.action.data.ActionData;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class GameResultData extends ActionData {
	
	private final GameResult result;
	private final Encodable object;
	
	public GameResultData(GameResult result, Encodable object) {
		super();
		this.result = result;
		this.object = object;
	}
	
	@SuppressWarnings("unchecked")
	public GameResultData(FriendlyByteBuffer buffer) {
		super(buffer);
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
	
	public GameResult getResult() {
		return this.result;
	}
	
	public Encodable getObject() {
		return this.object;
	}
	
}
