package fr.i360matt.fastmongo.v2.elements;

import fr.i360matt.fastmongo.v2.utils.Cache;
import fr.i360matt.fastmongo.v2.utils.Instantiate;
import fr.i360matt.fastmongo.v2.utils.SerializationChecks;
import fr.i360matt.fastmongo.v2.utils.SerializeBase64;
import org.bson.Document;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorableObject implements Serializable {

    private static final Map<Class<? extends StorableObject>, Cache> persistent  = new HashMap<>();

    protected Cache cache;

    protected Cache getCache () {
        if (this.cache == null) {
            this.cache = persistent.get(this.getClass());
            if (this.cache == null) {
                this.cache = new Cache(this.getClass());
                persistent.put(this.getClass(), this.cache);
            }
        }
        return this.cache;
    }

    protected void setCache (final Cache cache) {
        this.cache = cache;
    }


    /**
     * Allows you to convert a Document object to the fields of the current instance using the cache.
     * @param map The document retrieved from the database.
     */
    public void deserialize (final Map<String, Object> map) {
        for (final Field field : this.getCache().getFields()) {
            Object obj = map.getOrDefault(field.getName(), null);
            try {
                if (obj == null)
                    continue;
                if (obj instanceof Document) {
                    if (StorableObject.class.isAssignableFrom(field.getType())) {
                        StorableObject oneInstance = (StorableObject) Instantiate.newInstance(field.getType());
                        oneInstance.deserialize((Document) obj);
                        obj = oneInstance;
                    } else continue;
                } else if (!field.getName().equals("_id") && !SerializationChecks.isSerializable(field)) {
                    if (obj instanceof String) {
                        obj = SerializeBase64.deserialize((String) obj);
                    } else continue;
                }
                field.set(this, obj);
            } catch (final Exception e) {
                new RuntimeException("Cannot set value of type '" + obj.getClass().getName() + "' in field '" + field.getName() + "' of " + this.getClass().getName() + ".").printStackTrace();
            }
        }
    }


    /**
     * Allows to convert the fields of this current instance to the Document object using the cache.
     * @return The Document object.
     * @throws IllegalAccessException A field access exception.
     */
    public Map<String, Object> serialize () throws IllegalAccessException {
        final Map<String, Object> map = new HashMap<>();
        for (final Field field : this.getCache().getFields()) {

            try {
                if (field.getName().equals("_id"))
                    continue;
                Object value = field.get(this);
                if (value instanceof StorableObject) {
                    value = ((StorableObject) value).serialize();
                    map.put(field.getName(), value);
                } else if (!SerializationChecks.isSerializable(field)) {
                    if (value instanceof Serializable) {
                        value = SerializeBase64.serialize(value);
                        map.put(field.getName(), value);
                    } else {
                        throw new RuntimeException("Cannot serialize the field '" + field.getName() + "' of '" + this.getClass().getSimpleName() + "'.");
                    }
                } else {
                    map.put(field.getName(), value);
                }
            } catch (final RuntimeException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

}
