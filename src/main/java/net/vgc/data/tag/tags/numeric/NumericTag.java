package net.vgc.data.tag.tags.numeric;

import net.vgc.data.tag.Tag;

public abstract class NumericTag implements Tag {
	
	public abstract byte getAsByte();
	
	public abstract short getAsShort();
	
	public abstract int getAsInt();
	
	public abstract long getAsLong();

	public abstract float getAsFloat();

	public abstract double getAsDouble();

	public abstract Number getAsNumber();

	@Override
	public String toString() {
		return this.getAsString();
	}
	
}
