package net.luis.network.packet.listener;

import net.luis.application.GameApplication;
import net.luis.network.Connection;
import net.luis.network.packet.Packet;
import net.luis.utils.util.ReflectionHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static net.luis.network.packet.listener.PacketInvokHelper.createException;
import static net.luis.network.packet.listener.PacketInvokHelper.validateSignature;

/**
 *
 * @author Luis-st
 *
 */

public class PacketInvoker {
	
	public static void invoke(Connection connection, Packet packet) {
		ReflectionHelper.enableExceptionThrowing();
		GameApplication application = Objects.requireNonNull(GameApplication.getInstance());
		for (Class<?> clazz : PacketInvokHelper.getSubscribers(application.getApplicationType())) {
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
	
	private static void invokeStatic(Connection connection, GameApplication application, Packet packet, Method method) {
		PacketInvokHelper.setConnectionInstance(connection, method.getDeclaringClass(), null);
		Object[] objects = PacketInvokHelper.getPacketObjects(application, packet, method);
		int parameterCount = method.getParameterCount();
		if (parameterCount == 0) {
			ReflectionHelper.invoke(method, null);
		} else if (parameterCount == 1) {
			if (validateSignature(method, application)) {
				ReflectionHelper.invoke(method, null, application);
			} else {
				throw createException(method, application);
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
	
	private static void invokeNonStatic(Connection connection, Packet packet, Object instanceObject, Method method) {
		PacketInvokHelper.setConnectionInstance(connection, method.getDeclaringClass(), instanceObject);
		Object[] objects = PacketInvokHelper.getPacketObjects(null, packet, method);
		int parameterCount = method.getParameterCount();
		if (parameterCount == 0) {
			ReflectionHelper.invoke(method, instanceObject);
		} else if (parameterCount == 1 && method.getParameterTypes()[0].isInstance(packet)) {
			if (validateSignature(method, packet)) {
				ReflectionHelper.invoke(method, instanceObject, packet);
			} else {
				throw createException(method, packet);
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
