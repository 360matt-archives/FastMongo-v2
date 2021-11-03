package fr.i360matt.fastmongo.v2.utils;

import fr.i360matt.fastmongo.v2.annotations.ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Matt
 * Used to cache declared fields of a class, not including unused fields and statics.
 * This process greatly improves performance, significantly.
 */
public final class Cache {

    private final Field[] fields;
    private Field fieldID;

    public Cache (final Class<?> clazz) {
        final List<Field> temp = new ArrayList<>();

        for (final Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers()))
                continue;
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            try {
                if (!field.isAccessible())
                    field.setAccessible(true);

                if (this.fieldID == null) {
                    ID annotID = field.getAnnotation(ID.class);
                    if (annotID != null)
                        this.fieldID = field;
                }

                /*
                if (field.getName().equals("_id"))
                    continue;
                 */

               temp.add(field);

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        this.fields = temp.toArray(temp.toArray(new Field[0]));

    }


    public Field[] getFields () {
        return this.fields;
    }

    public Field getFieldID () {
        return this.fieldID;
    }

}
