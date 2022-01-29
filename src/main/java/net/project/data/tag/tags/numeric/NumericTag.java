package net.project.data.tag.tags.numeric;

import net.project.data.tag.Tag;

public abstract class NumericTag implements Tag {

	protected NumericTag() {
		
	}
	
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
