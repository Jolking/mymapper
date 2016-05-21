package com.mymapper.util;

import com.mymapper.annotation.Ignore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang on 3/30/16.
 */
public class ReflectionUtil {
    public static <T> T getFieldValue(Object object, String fullName) throws IllegalAccessException {
        return getFieldValue(object, fullName, false);
    }

    public static <T> T getFieldValue(Object object,
                                      String fieldName,
                                      boolean traceable) throws IllegalAccessException {
        Field field;
        String[] fieldNames = fieldName.split("\\.");
        for (String targetField : fieldNames) {
            field = searchField(object.getClass(), targetField, traceable);
            if (field == null)
                return null;
            object = getValue(object, field);
        }

        return (T) object;
    }

    private static Field searchField(Class c,
                                     String targetField,
                                     boolean traceable) {
        do {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (f.getName().equals(targetField)) {
                    return f;
                }
            }
            c = c.getSuperclass();
            traceable = traceable && c != Object.class;
        } while (traceable);

        return null;
    }

    private static Object getValue(Object target, Field field) throws IllegalAccessException {
        if (!field.isAccessible())
            field.setAccessible(true);
        return field.get(target);
    }

    public static void setFieldValue(Object target, String fieldName, Object
        value) {
        setFieldValue(target, fieldName, value, false);
    }

    public static void setFieldValue(Object target, String fieldName, Object
        value, boolean traceable) {
        Field field = searchField(target.getClass(), fieldName, traceable);
        if (field != null)
            setValue(field, target, value);
    }

    private static void setValue(Field field, Object target, Object value) {
        try {
            if (!field.isAccessible())
                field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> getNonNullMap(Class type, Object target) {
        Map<String, Object> map = new HashMap();
        Field[] fields = type.getDeclaredFields();
        try {
            Object value;
            for (Field field : fields) {
                if (field.isAnnotationPresent(Ignore.class))
                    continue;
                if (!field.isAccessible())
                    field.setAccessible(true);
                value = field.get(target);
                if (value != null) {
                    map.put(StringUtil.underline2Camel(field.getName()), value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<Field> getFieldList(Class c) {

        Field[] fields = c.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(fields.length);
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Ignore.class))
                    continue;
                if (!field.isAccessible())
                    field.setAccessible(true);
                fieldList.add(field);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return fieldList;
    }
}
