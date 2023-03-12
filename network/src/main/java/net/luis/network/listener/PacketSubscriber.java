package net.luis.network.listener;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketSubscriber {
	
	@NotNull
	String value() default "";
	
}
