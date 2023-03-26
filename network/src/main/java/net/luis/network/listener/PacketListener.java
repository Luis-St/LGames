package net.luis.network.listener;

import net.luis.network.packet.Packet;
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
public @interface PacketListener {
	
	@NotNull Class<?> value() default Packet.class;
	
}
