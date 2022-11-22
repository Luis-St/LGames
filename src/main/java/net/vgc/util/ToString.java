package net.vgc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class ToString {
	
	private final Object object;
	private final List<String> excludeFields;
	
	public ToString(Object object, String... excludeFields) {
		this.object = object;
		this.excludeFields = Lists.newArrayList(excludeFields);
	}
	
	public static String toString(Object object) {
		return new ToString(object).toString();
	}
	
	public static String toString(Object object, String... excludeFields) {
		return new ToString(object, excludeFields).toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.object.getClass().getSimpleName());
		List<Field> fields = this.getFields();
		if (fields.size() > 0) {
			builder.append("{");
			for (int i = 0; i < fields.size(); i++) {
				String name = fields.get(i).getName();
				if (!this.excludeFields.contains(name)) {
					if (i != 0) {
						builder.append(",");
					}
					builder.append(name).append("=").append(ReflectionHelper.get(fields.get(i), this.object).toString());
				}
			}
			builder.append("}");
		}
		return builder.toString();
	}
	
	private <T> List<Field> getFields() {
		List<List<Field>> allFields = new ArrayList<>();
		Class<?> clazz = this.object.getClass();
		while (clazz.getSuperclass() != null) {
			allFields.add(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		List<Field> fields = new ArrayList<>();
		for (List<Field> list : Util.reverseList(allFields)) {
			fields.addAll(list);
		}
		return fields;
	}
	
}
