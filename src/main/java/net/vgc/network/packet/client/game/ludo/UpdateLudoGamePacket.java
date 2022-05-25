package net.vgc.network.packet.client.game.ludo;

import java.util.List;

import com.google.common.collect.Table.Cell;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;
import net.vgc.game.ludo.player.LudoFigure;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.util.SimpleCell;

public class UpdateLudoGamePacket implements ClientPacket {
	
	protected final List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> figurePositions;
	
	public UpdateLudoGamePacket(List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> figurePositions) {
		this.figurePositions = figurePositions;
	}
	
	public UpdateLudoGamePacket(FriendlyByteBuffer buffer) {
		this.figurePositions = buffer.readList(buffer, (buf) -> {
			LudoFieldType type = buf.readEnum(LudoFieldType.class);
			LudoFieldPos pos = buf.read(LudoFieldPos.class);
			LudoFigure figure = buf.read(LudoFigure.class);
			return new SimpleCell<>(type, pos, figure);
		});
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(buffer, this.figurePositions, (buf, entry) -> {
			buf.writeEnum(entry.getRowKey());
			buf.write(entry.getColumnKey());
			buf.write(entry.getValue());
		});
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> getFigurePositions() {
		return this.figurePositions;
	}
	
}
