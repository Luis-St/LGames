package net.vgc.game.action.data.specific;

import java.util.List;

import net.vgc.game.action.data.GameActionData;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class FieldInfoData extends GameActionData {
	
	private final List<GameFieldInfo> fieldInfos;
	
	public FieldInfoData(List<GameFieldInfo> fieldInfos) {
		super();
		this.fieldInfos = fieldInfos;
	}
	
	public FieldInfoData(FriendlyByteBuffer buffer) {
		super(buffer);
		this.fieldInfos = buffer.readList(() -> {
			return buffer.read(GameFieldInfo.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldInfos, buffer::write);
	}
	
	public List<GameFieldInfo> getFieldInfos() {
		return this.fieldInfos;
	}
	
}
