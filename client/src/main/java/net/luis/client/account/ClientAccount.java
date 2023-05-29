package net.luis.client.account;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public record ClientAccount(String name, int id, String mail, UUID uniqueId, boolean guest) {
	
	public ClientAccount {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(mail, "Mail must not be null");
		Objects.requireNonNull(uniqueId, "Unique id must not be null");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientAccount that)) return false;
		
		if (this.id != that.id) return false;
		if (this.guest != that.guest) return false;
		if (!this.name.equals(that.name)) return false;
		if (!this.mail.equals(that.mail)) return false;
		return this.uniqueId.equals(that.uniqueId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.id, this.mail, this.uniqueId, this.guest);
	}
	
	@Override
	public @NotNull String toString() {
		return "ClientAccount{name='" + this.name + '\'' + ", id=" + this.id + ", mail='" + this.mail + '\'' + ", uniqueId=" + this.uniqueId + ", guest=" + this.guest + "}";
	}
	//endregion
	
	public @NotNull String toShortString() {
		return this.name + "#" + this.id;
	}
}
