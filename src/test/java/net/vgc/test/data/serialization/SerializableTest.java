package net.vgc.test.data.serialization;

import java.io.File;
import java.nio.file.Path;

import net.vgc.data.serialization.Serializable;
import net.vgc.data.serialization.SerializationUtil;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.test.IVGTest;
import net.vgc.test.VGCTest;
import net.vgc.test.VGCTestMain;

@VGCTest
public class SerializableTest implements IVGTest {
	
	protected final Path deserializeConstructorPath = new File("test/serialization/deserialize_constructor_test.txt").toPath();
	protected final Path loadMethodPath = new File("test/serialization/load_method_test.txt").toPath();
	protected final Path deserializeMethodPath = new File("test/serialization/deserialize_method_test.txt").toPath();
	protected final Path createMethodPath = new File("test/serialization/create_method_test.txt").toPath();
	protected final Path loadConstructorPath = new File("test/serialization/load_constructor_test.txt").toPath();
	
	@Override
	public void start() throws Exception {
		DeserializeConstructor deserializeConstructor = new DeserializeConstructor("deserialize constructor", 485754152);
		LOGGER.debug("{}" , deserializeConstructor);
		Tag.write(VGCTestMain.resourceDir.resolve(this.deserializeConstructorPath), deserializeConstructor.serialize());
		LoadMethod loadMethod = new LoadMethod("load method", 18832015);
		LOGGER.debug("{}" , loadMethod);
		Tag.write(VGCTestMain.resourceDir.resolve(this.loadMethodPath), loadMethod.serialize());
		DeserializeMethod deserializeMethod = new DeserializeMethod("deserialize method", 978232154);
		LOGGER.debug("{}" , deserializeMethod);
		Tag.write(VGCTestMain.resourceDir.resolve(this.deserializeMethodPath), deserializeMethod.serialize());
		CreateMethod createMethod = new CreateMethod("create method", 135024800);
		LOGGER.debug("{}" , createMethod);
		Tag.write(VGCTestMain.resourceDir.resolve(this.createMethodPath), createMethod.serialize());
		LoadConstructor loadConstructor = new LoadConstructor("load constructor", 96218721);
		LOGGER.debug("{}" , loadConstructor);
		Tag.write(VGCTestMain.resourceDir.resolve(this.loadConstructorPath), loadConstructor.serialize());
	}

	@Override
	public void stop() throws Exception {
		LOGGER.debug("{}", SerializationUtil.deserialize(DeserializeConstructor.class, (CompoundTag) Tag.load(VGCTestMain.resourceDir.resolve(this.deserializeConstructorPath))));
		LOGGER.debug("{}", SerializationUtil.deserialize(LoadMethod.class, (CompoundTag) Tag.load(VGCTestMain.resourceDir.resolve(this.loadMethodPath))));
		LOGGER.debug("{}", SerializationUtil.deserialize(DeserializeMethod.class, (CompoundTag) Tag.load(VGCTestMain.resourceDir.resolve(this.deserializeMethodPath))));
		LOGGER.debug("{}", SerializationUtil.deserialize(CreateMethod.class, (CompoundTag) Tag.load(VGCTestMain.resourceDir.resolve(this.createMethodPath))));
		LOGGER.debug("{}", SerializationUtil.deserialize(LoadConstructor.class, (CompoundTag) Tag.load(VGCTestMain.resourceDir.resolve(this.loadConstructorPath))));
	}
	
	static final class DeserializeConstructor implements Serializable {
		
		public final String string;
		public final int value;
		
		public DeserializeConstructor(String string, int value) {
			this.string = string;
			this.value = value;
		}
		
		public DeserializeConstructor(CompoundTag tag) {
			this.string = tag.getString("string_key");
			this.value = tag.getInt("int_key");
		}
		
		@Override
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("string_key", this.string);
			tag.putInt("int_key", this.value);
			return tag;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("string: ").append(this.string).append(", ");
			builder.append("value: ").append(this.value).append("");
			return builder.toString();
		}
		
	}
	
	static final class LoadMethod implements Serializable {
		
		public final String string;
		public final int value;
		
		public LoadMethod(String string, int value) {
			this.string = string;
			this.value = value;
		}
		
		public static LoadMethod load(CompoundTag tag) {
			return new LoadMethod(tag.getString("string_key"), tag.getInt("int_key"));
		}
		
		@Override
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("string_key", this.string);
			tag.putInt("int_key", this.value);
			return tag;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("string: ").append(this.string).append(", ");
			builder.append("value: ").append(this.value).append("");
			return builder.toString();
		}
		
	}
	
	static final class DeserializeMethod implements Serializable {

		public final String string;
		public final int value;
		
		public DeserializeMethod(String string, int value) {
			this.string = string;
			this.value = value;
		}
		
		public static DeserializeMethod deserialize(CompoundTag tag) {
			return new DeserializeMethod(tag.getString("string_key"), tag.getInt("int_key"));
		}
		
		@Override
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("string_key", this.string);
			tag.putInt("int_key", this.value);
			return tag;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("string: ").append(this.string).append(", ");
			builder.append("value: ").append(this.value).append("");
			return builder.toString();
		}
		
	}
	
	static final class CreateMethod implements Serializable {

		public final String string;
		public final int value;
		
		public CreateMethod(String string, int value) {
			this.string = string;
			this.value = value;
		}
		
		public static CreateMethod create(CompoundTag tag) {
			return new CreateMethod(tag.getString("string_key"), tag.getInt("int_key"));
		}
		
		@Override
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("string_key", this.string);
			tag.putInt("int_key", this.value);
			return tag;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("string: ").append(this.string).append(", ");
			builder.append("value: ").append(this.value).append("");
			return builder.toString();
		}
		
	}
	
	static final class LoadConstructor implements Serializable {

		public String string;
		public int value;
		
		public LoadConstructor() {
			
		}
		
		public LoadConstructor(String string, int value) {
			this.string = string;
			this.value = value;
		}
		
		@Override
		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("string_key", this.string);
			tag.putInt("int_key", this.value);
			return tag;
		}
		
		public void deserialize(CompoundTag tag) {
			this.string = tag.getString("string_key");
			this.value = tag.getInt("int_key");
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("string: ").append(this.string).append(", ");
			builder.append("value: ").append(this.value).append("");
			return builder.toString();
		}
		
	}

}
