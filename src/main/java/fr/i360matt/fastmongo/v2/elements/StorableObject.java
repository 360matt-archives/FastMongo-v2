package fr.i360matt.fastmongo.v2.elements;

import fr.i360matt.fastmongo.v2.utils.Cache;
import fr.i360matt.fastmongo.v2.utils.Instantiate;
import fr.i360matt.fastmongo.v2.utils.SerializeBase64;
import org.bson.Document;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
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
     * @throws IllegalAccessException A field access exception.
     */
    public void deserialize (final Map<String, Object> map) throws IllegalAccessException {
        for (final Field field : this.getCache().getFields()) {
            Object obj = map.getOrDefault(field.getName(), null);
            if (field.getName().equals("_id")) {
                field.set(this, obj);
            } else if (obj instanceof Document) {
                if (StorableObject.class.isAssignableFrom(field.getType())) {
                    StorableObject oneInstance = (StorableObject) Instantiate.newInstance(field.getType());
                    oneInstance.deserialize((Document) obj);
                    obj = oneInstance;
                }
                field.set(this, obj);
            } else if (!field.getType().isPrimitive() && field.getType() != String.class) {
                if (obj instanceof String) {
                    obj = SerializeBase64.deserialize((String) obj);
                    field.set(this, obj);
                }
            } else if (obj != null) {
                field.set(this, obj);
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
            if (field.getName().equals("_id"))
                continue;
            Object value = field.get(this);
            if (value instanceof StorableObject) {
                value = ((StorableObject) value).serialize();
            } else if (!field.getType().isPrimitive() && field.getType() != String.class) {
                if (value instanceof Serializable) {
                    value = SerializeBase64.serialize(value);
                }
            }

            map.put(field.getName(), value);
        }

        return map;
    }

}
