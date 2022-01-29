package net.project.data.tag.tags.numeric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.project.data.tag.TagType;
import net.project.data.tag.visitor.TagVisitor;

public class ShortTag extends NumericTag {
	
	public static final TagType<ShortTag> TYPE = new TagType<ShortTag>() {
		@Override
		public ShortTag load(DataInput input) throws IOException {
			return valueOf(input.readShort());
		}
		
		@Override
		public String getName() {
			return "short_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "ShortTag";
		}
		
		@Override
		public boolean isValue() {
			return true;
		}
	};
	
	protected final short data;
	
	ShortTag(short data) {
		this.data = data;
	}
	
	public static ShortTag valueOf(short data) {
		return new ShortTag(data);
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.data);
	}

	@Override
	public byte getId() {
		return SHORT_TAG;
	}

	@Override
	public TagType<ShortTag> getType() {
		return TYPE;
	}

	@Override
	public ShortTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitShort(this);
	}

	@Override
	public byte getAsByte() {
		return (byte) this.data;
	}

	@Override
	public short getAsShort() {
		return (short) this.data;
	}

	@Override
	public int getAsInt() {
		return (int) this.data;
	}

	@Override
	public long getAsLong() {
		return (long) this.data;
	}

	@Override
	public float getAsFloat() {
		return (float) this.data;
	}
	
	@Override
	public double getAsDouble() {
		return (double) this.data;
	}

	@Override
	public Number getAsNumber() {
		return this.data;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof ShortTag tag) {
			return this.getAsShort() == tag.getAsShort();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Short.hashCode(this.data);
	}
	
}