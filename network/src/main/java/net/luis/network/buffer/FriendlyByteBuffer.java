package net.luis.network.buffer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.util.Utils;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

public class FriendlyByteBuffer {
	
	private static final Logger LOGGER = LogManager.getLogger(FriendlyByteBuffer.class);
	
	private final ByteBuf buffer;
	
	public FriendlyByteBuffer() {
		this(Unpooled.buffer());
	}
	
	public FriendlyByteBuffer(@NotNull ByteBuf buffer) {
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
	
	public void writeString(@NotNull String value) {
		this.buffer.writeInt(value.length());
		this.buffer.writeCharSequence(value, StandardCharsets.UTF_8);
	}
	
	public @NotNull String readString() {
		int length = this.buffer.readInt();
		return this.buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
	}
	
	public void writeUUID(@NotNull UUID value) {
		this.writeLong(value.getMostSignificantBits());
		this.writeLong(value.getLeastSignificantBits());
	}
	
	public @NotNull UUID readUUID() {
		long most = this.readLong();
		long least = this.readLong();
		UUID uuid = new UUID(most, least);
		return uuid.equals(Utils.EMPTY_UUID) ? Utils.EMPTY_UUID : uuid;
	}
	
	public <T extends Encodable> void write(@NotNull T value) {
		value.encode(this);
	}
	
	public <T extends Encodable> @Nullable T read(@NotNull Class<T> clazz) {
		if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
			Constructor<T> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
			assert constructor != null;
			if (constructor.isAnnotationPresent(DecodingConstructor.class)) {
				T value = ReflectionHelper.newInstance(constructor, this);
				if (value != null) {
					return value;
				} else {
					LOGGER.error("Failed to read object of type {} from buffer because an error occurred while creating a new instance of class {}", clazz.getSimpleName(), clazz.getName());
				}
			} else {
				LOGGER.error("Failed to read object of type {} from buffer because decoding constructor is not annotated with @DecodingConstructor", clazz.getSimpleName());
			}
		} else {
			LOGGER.warn("Failed to read an object of type {} from the buffer because there is no FriendlyByteBuffer constructor", clazz.getSimpleName());
		}
		return null;
	}
	
	public <T extends Enum<T>> void writeEnum(@NotNull T value) {
		this.writeInt(value.ordinal());
	}
	
	public <T extends Enum<T>> @NotNull T readEnum(@NotNull Class<T> clazz) {
		int ordinal = this.readInt();
		return clazz.getEnumConstants()[ordinal];
	}
	
	public <T> void writeList(@NotNull List<T> list, @NotNull Consumer<T> encoder) {
		this.writeInt(list.size());
		for (T t : list) {
			encoder.accept(t);
		}
	}
	
	public <T> @NotNull List<T> readList(@NotNull Supplier<T> decoder) {
		List<T> list = Lists.newArrayList();
		int size = this.readInt();
		for (int i = 0; i < size; i++) {
			list.add(decoder.get());
		}
		return list;
	}
	
	public <K, V> void writeMap(@NotNull Map<K, V> map, @NotNull Consumer<K> keyEncoder, @NotNull Consumer<V> valueEncoder) {
		this.writeInt(map.size());
		for (Map.Entry<K, V> entry : map.entrySet()) {
			keyEncoder.accept(entry.getKey());
			valueEncoder.accept(entry.getValue());
		}
	}
	
	public <K, V> @NotNull Map<K, V> readMap(@NotNull Supplier<K> keyDecoder, @NotNull Supplier<V> valueDecoder) {
		Map<K, V> map = Maps.newHashMap();
		int size = this.buffer.readInt();
		for (int i = 0; i < size; i++) {
			K key = keyDecoder.get();
			V value = valueDecoder.get();
			map.put(key, value);
		}
		return map;
	}
	
	public <T extends Encodable> void writeInterface(@NotNull T value) {
		this.writeString(value.getClass().getName());
		this.write(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Encodable> @Nullable T readInterface() {
		String className = this.readString();
		return this.read((Class<T>) Objects.requireNonNull(ReflectionHelper.getClassForName(className)));
	}
	
	public <T extends Enum<T>> void writeEnumInterface(@NotNull T value) {
		this.writeString(value.getClass().getName());
		this.writeEnum(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> @NotNull T readEnumInterface() {
		String className = this.readString();
		return this.readEnum((Class<T>) Objects.requireNonNull(ReflectionHelper.getClassForName(className)));
	}
	
	public @NotNull ByteBuf toBuffer() {
		return this.buffer;
	}
	
}
