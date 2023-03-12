package net.luis.client.account;

import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public record ClientAccount(@NotNull String name, int id, @NotNull String mail, @NotNull UUID uuid, boolean guest) {
	
	@Override
	public boolean equals(@Nullable Object o) {
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
	public @NotNull String toString() {
		return ToString.toString(this);
	}
}
