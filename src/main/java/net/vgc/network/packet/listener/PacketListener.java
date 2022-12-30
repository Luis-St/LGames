package net.vgc.network.packet.listener;

import net.vgc.network.packet.Packet;

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
