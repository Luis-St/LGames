package net.vgc.client;

import net.luis.utils.util.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public record ClientAccount(String name, int id, String mail, UUID uuid, boolean guest) {
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientAccount that)) return false;
		
		if (this.id != that.id) return false;
		if (this.guest != that.guest) return false;
		if (!this.name.equals(that.name)) return false;
		if (!this.mail.equals(that.mail)) return false;
		return this.uuid.equals(that.uuid);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.id, this.mail, this.uuid, this.guest);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
