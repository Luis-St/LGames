package net.luis.network.packet.listener;

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
	
	String value() default "";
	
}
