package com.wizardlybump17.vehicles.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtil {

    public static Object invokeMethod(Class<?> clazz, Object object, String name, Class<?>[] paramTypes, Object... params) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramTypes);
            method.setAccessible(true);
            return method.invoke(object, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Class<?> clazz, Object object, String name, Object value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, Object object, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) box(field.get(object));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object box(Object object) {
        if (object.getClass() == int.class)
            return Integer.valueOf((int) object);
        if (object.getClass() == double.class)
            return Double.valueOf((double) object);
        if (object.getClass() == float.class)
            return Float.valueOf((float) object);
        if (object.getClass() == long.class)
            return Long.valueOf((long) object);
        if (object.getClass() == short.class)
            return Short.valueOf((short) object);
        if (object.getClass() == byte.class)
            return Byte.valueOf((byte) object);
        if (object.getClass() == boolean.class)
            return Boolean.valueOf((boolean) object);
        if (object.getClass() == char.class)
            return Character.valueOf((char) object);
        return object;
    }
}
