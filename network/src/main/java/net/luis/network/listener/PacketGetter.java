package net.luis.network.listener;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketGetter {
	
	@NotNull String getterPrefix() default "";
	
	@NotNull String parameterName() default "";
	
}
