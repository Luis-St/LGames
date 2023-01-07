package net.vgc.network.buffer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.luis.utils.util.ReflectionHelper;
import net.luis.utils.util.Utils;
import net.vgc.util.EnumRepresentable;
import net.vgc.util.annotation.DecodingConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

public class FriendlyByteBuffer {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ByteBuf buffer;
	
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
		return uuid.equals(Utils.EMPTY_UUID) ? Utils.EMPTY_UUID : uuid;
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
	
	public <T extends EnumRepresentable> void writeEnum(T value) {
		this.writeInt(value.getId());
	}
	
	public <T extends EnumRepresentable> T readEnum(Class<T> clazz) {
		int id = this.readInt();
		return EnumRepresentable.fromId(clazz, id);
	}
	
	public <T> void writeList(List<T> list, Consumer<T> encoder) {
		this.writeInt(list.size());
		for (T t : list) {
			encoder.accept(t);
		}
	}
	
	public <T> List<T> readList(Supplier<T> decoder) {
		List<T> list = Lists.newArrayList();
		int size = this.readInt();
		for (int i = 0; i < size; i++) {
			list.add(decoder.get());
		}
		return list;
	}
	
	public <K, V> void writeMap(Map<K, V> map, Consumer<K> keyEncoder, Consumer<V> valueEncoder) {
		this.writeInt(map.size());
		for (Map.Entry<K, V> entry : map.entrySet()) {
			keyEncoder.accept(entry.getKey());
			valueEncoder.accept(entry.getValue());
		}
	}
	
	public <K, V> Map<K, V> readMap(Supplier<K> keyDecoder, Supplier<V> valueDecoder) {
		Map<K, V> map = Maps.newHashMap();
		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			K key = keyDecoder.get();
			V value = valueDecoder.get();
			map.put(key, value);
		}
		return map;
	}
	
	public <T extends Encodable> void writeOptional(Optional<T> optional) {
		this.writeBoolean(optional.isPresent());
		optional.ifPresent((value) -> {
			this.write(value);
		});
	}
	
	public <T extends Encodable> Optional<T> readOptional(Class<T> clazz) {
		boolean present = this.readBoolean();
		if (present) {
			T value = this.read(clazz);
			return Optional.of(value);
		}
		return Optional.empty();
	}
	
	public <T extends EnumRepresentable> void writeEnumOptional(Optional<T> optional) {
		this.writeBoolean(optional.isPresent());
		optional.ifPresent((value) -> {
			this.writeEnum(value);
		});
	}
	
	public <T extends EnumRepresentable> Optional<T> readEnumOptional(Class<T> clazz) {
		boolean present = this.readBoolean();
		if (present) {
			T value = this.readEnum(clazz);
			return Optional.of(value);
		}
		return Optional.empty();
	}
	
	public <T extends Encodable> void writeInterface(T value) {
		this.writeString(value.getClass().getName());
		this.write(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Encodable> T readInterface() {
		String className = this.readString();
		return this.read((Class<T>) ReflectionHelper.getClassForName(className));
	}
	
	public <T extends EnumRepresentable> void writeEnumInterface(T value) {
		this.writeString(value.getClass().getName());
		this.writeEnum(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EnumRepresentable> T readEnumInterface() {
		String className = this.readString();
		return this.readEnum((Class<T>) ReflectionHelper.getClassForName(className));
	}
	
	public ByteBuf toBuffer() {
		return this.buffer;
	}
	
}
