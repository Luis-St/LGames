package net.project.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.project.data.tag.Tag;
import net.project.data.tag.TagType;
import net.project.data.tag.visitor.TagVisitor;

public class EndTag implements Tag {
	
	public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
		@Override
		public EndTag load(DataInput input) throws IOException {
			return INSTANCE;
		}
		
		@Override
		public String getName() {
			return "end_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "EndTag";
		}
	};
	public static final EndTag INSTANCE = new EndTag();
	
	private EndTag() {
		
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		
	}

	@Override
	public byte getId() {
		return END_TAG;
	}

	@Override
	public TagType<EndTag> getType() {
		return TYPE;
	}

	@Override
	public EndTag copy() {
		return this;
	}

	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitEnd(this);
	}
	
	@Override
	public String toString() {
		return this.getAsString();
	}

}
