package net.vgc.game.action;

import java.util.function.Function;

import net.vgc.game.action.data.ActionData;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.ToString;

public class ActionType<T extends Action<V>, V extends ActionData> {
	
	private final String name;
	private final int id;
	private final ActionFactory<T, V> factory;
	private final Function<FriendlyByteBuffer, V> decoder;
	
	public ActionType(String name, int id, ActionFactory<T, V> factory, Function<FriendlyByteBuffer, V> decoder) {
		this.name = name;
		this.id = id;
		this.factory = factory;
		this.decoder = decoder;
	}
	
	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public T create(V data) {
		return this.factory.create(data);
	}
	
	public T decode(FriendlyByteBuffer buffer) {
		return this.create(this.decoder.apply(buffer));
	}
	
	public void send(V data) {
		
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "factory", "decoder");
	}
	
}
