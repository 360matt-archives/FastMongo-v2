package fr.i360matt.fastmongo.v2.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class SerializationChecks {

    public static boolean isSerializable (final Field field) {
        return isSerializable(field, field.getType());
    }

    private static boolean isSerializable (final Field field, final Class<?> type) {
        if (type.isPrimitive())
            return true;
        if (type == String.class)
            return true;
        if (type == String[].class)
            return true;
        if (Number[].class.isAssignableFrom(type))
            return true;


        if (ArrayList.class.isAssignableFrom(type) || HashMap.class.isAssignableFrom(type)) {
            if (field == null)
                return true;
            Type genericType = field.getGenericType();
            try {
             //   if (!genericType.equals(field.getType()))
                    recurseGenericTypes(genericType);
                return true;
            } catch (final RuntimeException e) {
                return false;
            }
        }
        return false;
    }


    private static void recurseGenericTypes (Object parameterizedType) {
        if (parameterizedType instanceof ParameterizedType) {
            final Type[] currentTypes = ((ParameterizedType) parameterizedType).getActualTypeArguments();
            for (Type type : currentTypes) {
                if (type instanceof ParameterizedType) {
                    recurseGenericTypes(type);
                } else if (type instanceof Class) {
                    if (!isSerializable(null, (Class<?>) type)) {
                        throw new RuntimeException("Cannot serialize " + ((Class<?>) type).getName());
                    }
                }

                if (type.getTypeName().startsWith("?")) {
                    throw new RuntimeException("Cannot serialize ");
                }
            }
        }
    }
}
