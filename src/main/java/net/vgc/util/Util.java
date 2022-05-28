package net.vgc.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import net.vgc.Constans;
import net.vgc.util.streams.DebugPrintStream;
import net.vgc.util.streams.InfoPrintStream;

public class Util {
	
	protected static final Logger LOGGER = LogManager.getLogger(Util.class);
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static <T> T make(T object, Consumer<T> consumer) {	
		consumer.accept(object);
		return object;
	}
	
	public static Timeline createTicker(String name, Tickable tickable) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(50), name, (event) -> {
			tickable.tick();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		return timeline;
	}
	
	public static void runDelayed(String name, int millis, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> {
			action.run();
		}));
		timeline.play();
	}
	
	public static void runCycled(String name, int millis, int cycleCount, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> {
			action.run();
		}));
		timeline.setCycleCount(cycleCount);
		timeline.play();
	}
	
	public static void runAfter(String name, int delay, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(0), name, (event) -> {
			action.run();
		}));
		timeline.setDelay(Duration.millis(delay));
		timeline.play();
	}
	
	public static <K, V, T> List<T> mapToList(Map<K, V> map, BiFunction<K, V, T> function) {
		List<T> list = Lists.newArrayList();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			list.add(function.apply(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	public static <T, K, V> Map<K, V> listToMap(List<T> list, Function<T, Entry<K, V>> function) {
		Map<K, V> map = Maps.newHashMap();
		for (T t : list) {
			Entry<K, V> entry = function.apply(t);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public static <T, U> List<U> mapList(List<T> list, Function<T, U> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <T, U, V> List<V> mapList(List<T> list, Function<T, U> firstFunction, Function<U, V> secondFunction) {
		return list.stream().map(firstFunction).map(secondFunction).collect(Collectors.toList());
	}
	
	public static <K, T, V> Map<T, V> mapKey(Map<K, V> map, Function<K, T> function) {
		Map<T, V> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(function.apply(entry.getKey()), entry.getValue());
		}
		return mapped;
	}
	
	public static <K, V, T> Map<K, T> mapValue(Map<K, V> map, Function<V, T> function) {
		Map<K, T> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return mapped;
	}
	
	public static <T> List<T> reverseList(List<T> list) {
		List<T> reversedList = Lists.newArrayList();
		for (int i = list.size(); i-- > 0;) {
			reversedList.add(list.get(i));
		}
		return reversedList;
	}
	
	@Nullable
	public static <T, R> R runIfNotNull(T value, Function<T, R> function) {
		if (value != null) {
			return function.apply(value);
		}
		return null;
	}
	
	public static Random systemRandom() {
		return new Random(System.currentTimeMillis());
	}
	
	@SafeVarargs
	public static <T> List<T> concatLists(List<T>... lists) {
		List<T> list = Lists.newArrayList();
		for (List<T> t : lists) {
			list.addAll(t);
		}
		return list;
	}
	
	public static void warpStreams(boolean debugMode) {
		LOGGER.info("Warp System PrintStreams to type {}", debugMode ? "DEBUG" : "INFO");
		Constans.DEBUG = debugMode;
		if (debugMode) {
			System.setOut(new DebugPrintStream("STDOUT", System.out));
			System.setErr(new DebugPrintStream("STDERR", System.err));
		} else {
			System.setOut(new InfoPrintStream("STDOUT", System.out));
			System.setErr(new InfoPrintStream("STDERR", System.err));
		}
	}
	
}
