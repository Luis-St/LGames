package net.vgc.network.buffer;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.vgc.util.EnumRepresentable;
import net.vgc.util.ReflectionHelper;
import net.vgc.util.Util;
import net.vgc.util.annotation.DecodingConstructor;

public class FriendlyByteBuffer {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final ByteBuf buffer;
	
	public FriendlyByteBuffer() {
		this(Unpooled.buffer());
	}
	
	public FriendlyByteBuffer(ByteBuf buffer) {
		this.buffer = buffer;
	}
	
	public int readableBytes() {
		return this.buffer.readableBytes();
	}
	
	public int writerIndex() {
		return this.buffer.writerIndex();
	}
	
	public void writeByte(byte value) {
		this.buffer.writeByte(value);
	}
	
	public byte readByte() {
		return this.buffer.readByte();
	}
	
	public void writeShort(short value) {
		this.buffer.writeShort(value);
	}
	
	public short readShort() {
		return this.buffer.readShort();
	}
	
	public void writeInt(int value) {
		this.buffer.writeInt(value);
	}
	
	public int readInt() {
		return this.buffer.readInt();
	}
	
	public void writeLong(long value) {
		this.buffer.writeLong(value);
	}
	
	public long readLong() {
		return this.buffer.readLong();
	}
	
	public void writeFloat(float value) {
		this.buffer.writeFloat(value);
	}
	
	public float readFloat() {
		return this.buffer.readFloat();
	}
	
	public void writeDouble(double value) {
		this.buffer.writeDouble(value);
	}
	
	public double readDouble() {
		return this.buffer.readShort();
	}
	
	public void writeBoolean(boolean value) {
		this.buffer.writeBoolean(value);
	}
	
	public boolean readBoolean() {
		return this.buffer.readBoolean();
	}
	
	public void writeString(String value) {
		this.buffer.writeInt(value.length());
		this.buffer.writeCharSequence(value, StandardCharsets.UTF_8);
	}
	
	public String readString() {
		int length = this.buffer.readInt();
		return this.buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
	}
	
	public void writeUUID(UUID value) {
		this.writeLong(value.getMostSignificantBits());
		this.writeLong(value.getLeastSignificantBits());
	}
	
	public UUID readUUID() {
		long most = this.readLong();
		long least = this.readLong();
		UUID uuid = new UUID(most, least);
		return uuid.equals(Util.EMPTY_UUID) ? Util.EMPTY_UUID : uuid;
	}
	
	public <T extends Encodable> void write(T value) {
		value.encode(this);
	}
	
	public <T extends Encodable> T read(Class<T> clazz) {
		if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
			Constructor<T> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
			if (constructor.isAnnotationPresent(DecodingConstructor.class)) {
				T value = ReflectionHelper.newInstance(constructor, this);
				if (value != null) {
					return value;
				} else {
					LOGGER.warn("Fail to read object of type {} from buffer, since there was an error in creating a new instance of the class {}", clazz.getSimpleName(), clazz.getName());
				}
			} else {
				LOGGER.warn("Fail to read object of type {} from buffer, since the decode constructor is not annotated with @DecodingConstructor", clazz.getSimpleName());
			}
		} else {
			LOGGER.warn("Fail to read object of type {} from buffer, since there is no FriendlyByteBuffer constructor", clazz.getSimpleName());
		}
		return null;
	}
	
	public <T extends Enum<T> & EnumRepresentable> void writeEnum(T value) {
		this.writeInt(value.getId());
	}
	
	public <T extends Enum<T> & EnumRepresentable> T readEnum(Class<T> clazz) {
		int id = this.readInt();
		return EnumRepresentable.fromId(clazz, id);
	}
	
	public <T> void writeList(FriendlyByteBuffer buffer, List<T> list, BiConsumer<FriendlyByteBuffer, T> consumer) {
		buffer.writeInt(list.size());
		for (T t : list) {
			consumer.accept(buffer, t);
		}
	}
	
	public <T> List<T> readList(FriendlyByteBuffer buffer, Function<FriendlyByteBuffer, T> function) {
		List<T> list = Lists.newArrayList();
		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			list.add(function.apply(buffer));
		}
		return list;
	}
	
	public <K, V> void writeMap(FriendlyByteBuffer buffer, Map<K, V> map, BiConsumer<FriendlyByteBuffer, K> keyConsumer, BiConsumer<FriendlyByteBuffer, V> valueConsumer) {
		buffer.writeInt(map.size());
		for (Map.Entry<K, V> entry : map.entrySet()) {
			keyConsumer.accept(buffer, entry.getKey());
			valueConsumer.accept(buffer, entry.getValue());
		}
	}
	
	public <K, V> Map<K, V> readMap(FriendlyByteBuffer buffer, Function<FriendlyByteBuffer, K> keyFunction, Function<FriendlyByteBuffer, V> valueFunction) {
		Map<K, V> map = Maps.newHashMap();
		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			K key = keyFunction.apply(buffer);
			V value = valueFunction.apply(buffer);
			map.put(key, value);
		}
		return map;
	}
	
	public ByteBuf toBuffer() {
		return this.buffer;
	}
	
}
