package net.vgc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import com.google.common.collect.Lists;

public class ClasspathInspector {
	
	public static List<Class<?>> getTestClasses() {
		List<Class<?>> classes = getClasses();
		classes.removeIf(clazz -> !isTestClass(clazz));
		return classes;
	}
	
	public static List<Class<?>> getMainClasses() {
		List<Class<?>> classes = getClasses();
		classes.removeIf(clazz -> isTestClass(clazz));
		return classes;
	}
	
	protected static boolean isTestClass(Class<?> clazz) {
		String[] packages = clazz.getPackageName().split("\\.");
		if (packages.length >= 3) {
			return packages[2].contains("test");
		}
		return false;
	}
	
	protected static List<Class<?>> getClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : getClasspathClasses()) {
			if (file.isDirectory()) {
				classes.addAll(getClasses(file));
			}
		}
		classes.removeIf(clazz -> clazz.isAnonymousClass());
		return classes;
	}
	
	protected static List<Class<?>> getClasses(File path) {
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : getFiles(path, (dir, name) -> name.endsWith(".class"), true)) {
			try {
				classes.add(Class.forName(getClassName(file.getAbsolutePath().substring(path.getAbsolutePath().length() + 1))));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return classes;
	}
	
	protected static List<File> getFiles(File directory, FilenameFilter filter, boolean recurse) {
		List<File> files = Lists.newArrayList();
		for (File file : directory.listFiles()) {
			if (filter == null || filter.accept(directory, file.getName())) {
				files.add(file);
			}
			if (recurse && file.isDirectory()) {
				files.addAll(getFiles(file, filter, recurse));
			}
		}
		return files;
	}
	
	protected static String getClassName(final String fileName) {
		return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
	}

	protected static List<File> getClasspathClasses() {
		List<File> files = Lists.newArrayList();
		if (System.getProperty("java.class.path") != null) {
			for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
				files.add(new File(path));
			}
		}
		return files;
	}
	
}
