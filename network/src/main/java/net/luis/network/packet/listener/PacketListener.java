package net.luis.network.packet.listener;

import net.luis.network.packet.Packet;

import java.lang.annotation.*;

/**
 *
 * @author Luis-st
 *
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketListener {
	
	Class<?> value() default Packet.class;
	
}
