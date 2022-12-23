package net.vgc.network.packet.listener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.vgc.network.packet.Packet;

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
