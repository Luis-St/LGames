package net.luis.network.listener;

import net.luis.network.Connection;
import net.luis.network.packet.Packet;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class PacketInvoker {
	
	private static final Class<?> GAME_APPLICATION_CLASS = Objects.requireNonNull(ReflectionHelper.getClassForName("net.luis.game.application.GameApplication"));
	
	public static void invoke(@NotNull Connection connection, @NotNull Packet packet) {
		ReflectionHelper.enableExceptionThrowing();
		Object application = ReflectionHelper.invoke(Objects.requireNonNull(ReflectionHelper.getMethod(GAME_APPLICATION_CLASS, "getInstance")), null);
		assert GAME_APPLICATION_CLASS.isInstance(application);
		Object type = ReflectionHelper.invoke(Objects.requireNonNull(ReflectionHelper.getMethod(GAME_APPLICATION_CLASS, "getApplicationType")), application);
		for (Class<?> clazz : PacketInvokHelper.getSubscribers(type)) {
			Object instanceObject = PacketInvokHelper.getInstanceObject(application, clazz, clazz.getAnnotation(PacketSubscriber.class));
			for (Method method : PacketInvokHelper.getListeners(clazz, packet)) {
				if (Modifier.isStatic(method.getModifiers())) {
					invokeStatic(connection, application, packet, method);
				} else if (instanceObject != null) {
					if (instanceObject.getClass() == method.getDeclaringClass()) {
						invokeNonStatic(connection, packet, instanceObject, method);
					} else {
						throw new RuntimeException("The instance class does not declare a method " + method.getDeclaringClass().getName() + "#" + method.getName());
					}
				}
			}
		}
		ReflectionHelper.disableExceptionThrowing();
	}
	
	private static void invokeStatic(@NotNull Connection connection, @NotNull Object application, @NotNull Packet packet, @NotNull Method method) {
		PacketInvokHelper.setConnectionInstance(connection, method.getDeclaringClass(), null);
		Object[] objects = PacketInvokHelper.getPacketObjects(application, packet, method);
		int parameterCount = method.getParameterCount();
		if (parameterCount == 0) {
			ReflectionHelper.invoke(method, null);
		} else if (parameterCount == 1) {
			if (PacketInvokHelper.validateSignature(method, application)) {
				ReflectionHelper.invoke(method, null, application);
			} else {
				throw PacketInvokHelper.createException(method, application);
			}
		} else if (parameterCount == 2 && method.getParameterTypes()[1].isInstance(packet)) {
			if (PacketInvokHelper.validateSignature(method, application, packet)) {
				ReflectionHelper.invoke(method, null, application, packet);
			} else {
				throw PacketInvokHelper.createException(method, application, packet);
			}
		} else if (method.getAnnotation(PacketListener.class).value() == packet.getClass()) {
			if (PacketInvokHelper.validateSignature(method, objects)) {
				ReflectionHelper.invoke(method, null, objects);
			} else {
				throw PacketInvokHelper.createException(method, objects);
			}
		}
		PacketInvokHelper.setConnectionInstance(null, method.getDeclaringClass(), null);
	}
	
	private static void invokeNonStatic(@NotNull Connection connection, @NotNull Packet packet, @Nullable Object instanceObject, @NotNull Method method) {
		PacketInvokHelper.setConnectionInstance(connection, method.getDeclaringClass(), instanceObject);
		Object[] objects = PacketInvokHelper.getPacketObjects(null, packet, method);
		int parameterCount = method.getParameterCount();
		if (parameterCount == 0) {
			ReflectionHelper.invoke(method, instanceObject);
		} else if (parameterCount == 1 && method.getParameterTypes()[0].isInstance(packet)) {
			if (PacketInvokHelper.validateSignature(method, packet)) {
				ReflectionHelper.invoke(method, instanceObject, packet);
			} else {
				throw PacketInvokHelper.createException(method, packet);
			}
		} else if (method.getAnnotation(PacketListener.class).value() == packet.getClass()) {
			if (PacketInvokHelper.validateSignature(method, objects)) {
				ReflectionHelper.invoke(method, instanceObject, objects);
			} else {
				throw PacketInvokHelper.createException(method, objects);
			}
		}
		PacketInvokHelper.setConnectionInstance(null, method.getDeclaringClass(), instanceObject);
	}
	
}