package net.vgc.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	
	R apply(T t, U u, V v);

	default <S> TriFunction<T, U, V, S> andThen(Function<? super R, ? extends S> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> {
			return after.apply(this.apply(t, u, v));
		};
	}
	
}
