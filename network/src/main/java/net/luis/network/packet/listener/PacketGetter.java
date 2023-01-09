package net.luis.network.packet.listener;

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
	
	String getterPrefix() default "";
	
	String parameterName() default "";
	
}
