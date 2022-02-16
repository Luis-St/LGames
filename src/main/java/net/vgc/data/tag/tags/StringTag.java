package net.project.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import net.project.data.tag.Tag;
import net.project.data.tag.TagType;
import net.project.data.tag.visitor.TagVisitor;

public class StringTag implements Tag {
	
	public static final TagType<StringTag> TYPE = new TagType<StringTag>() {
		@Override
		public StringTag load(DataInput input) throws IOException {
			return StringTag.valueOf(input.readUTF());
		}
		
		@Override
		public String getName() {
			return "string_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "StringTag";
		}
	};
	public static final StringTag EMPTY = new StringTag("");
	
	protected final String data;
	
	StringTag(String data) {
		this.data = data;
	}
	
	public static StringTag valueOf(String data) {
		if (data == null) {
			return new StringTag("");
		}
		return data.isEmpty() ? EMPTY : new StringTag(data);
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.data);
	}

	@Override
	public byte getId() {
		return STRING_TAG;
	}

	@Override
	public TagType<StringTag> getType() {
		return TYPE;
	}

	@Override
	public StringTag copy() {
		return valueOf(this.data);
	}

	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitString(this);
	}
	
	@Override
	public String getAsString() {
		return this.data;
	}
	
	@Override
	public String toString() {
		return Tag.super.getAsString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object instanceof StringTag tag && Objects.equals(this.data, tag.data);
		}
	}
	
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}

}
