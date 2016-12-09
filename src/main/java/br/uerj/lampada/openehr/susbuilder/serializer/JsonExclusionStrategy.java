package br.uerj.lampada.openehr.susbuilder.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		Constructor[] array = clazz.getConstructors();
		for (Constructor constructor : array) {
			if (constructor.isAnnotationPresent(FullConstructor.class)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		Class clazz = f.getDeclaringClass();
		if (clazz == null) {
			return false;
		}
		Constructor[] array = clazz.getConstructors();
		for (Constructor constructor : array) {
			if (constructor.isAnnotationPresent(FullConstructor.class)) {
				Annotation[][] annotations = constructor
						.getParameterAnnotations();
				if (annotations != null) {
					for (int i = 0; i < annotations.length; i++) {
						if (annotations[i].length == 0) {
							throw new IllegalArgumentException(
									"missing annotations of attribute " + i);
						}
						Attribute attribute = (Attribute) annotations[i][0];
						if (attribute.name().equals(f.getName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}