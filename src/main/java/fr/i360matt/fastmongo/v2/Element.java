package fr.i360matt.fastmongo.v2;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * This class is used to load document data from MongoDB, manipulate it and then save it in the DB.
 *
 * @author 360matt
 * @version 2.0.0
 */
public class Element implements Closeable, Serializable {
    protected static final UpdateOptions UPSERT = new UpdateOptions().upsert(true);

    protected Manager<?> manager;

    private Bson filter;
    private Object cache_id;

    public Element (final Manager<?> manager) {
        this.manager = manager;
    }

    public Bson getFilter () {
        if (this.filter == null)
            this.filter = Filters.eq(manager.getFieldID().getName(), getID());
        return this.filter;
    }

    public Object getID () {
        if (this.cache_id == null) {
            try {
                this.cache_id = this.manager.getFieldID().get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.cache_id;
    }

    /**
     * Allows you to convert a Document object to the fields of the current instance using the cache.
     * @param document The document retrieved from the database.
     * @throws IllegalAccessException A field access exception.
     */
    public void docToField (final Document document) throws IllegalAccessException {
        for (final Field field : this.manager.getFieldsCache()) {
            final Object obj = document.getOrDefault(field.getName(), null);
            if (obj != null) {
                field.set(this, obj);
            }
        }
    }

    /**
     * Allows to convert the fields of this current instance to the Document object using the cache.
     * @return The Document object.
     * @throws IllegalAccessException A field access exception.
     */
    public Document fieldToDoc () throws IllegalAccessException {
        final Document document = new Document(this.manager.getFieldID().getName(), this.getID());
        for (final Field field : this.manager.getFieldsCache())
            document.put(field.getName(), field.get(this));
        return document;
    }

    public Manager<?> getManager () {
        return manager;
    }

    protected void setManager (final Manager<?> manager) {
        this.manager = manager;
    }

    // ____________________________________________________________________________________________

    /**
     * Allows to load the data.
     */
    public void load () {
        if (this instanceof Custom)
            ((Custom) this).customLoad();

        final Document doc = manager.collection.find(this.getFilter()).first();
        if (doc != null) {
            try {
                this.docToField(doc);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allow to save the data.
     */
    public void save () {
        if (this instanceof Custom)
            ((Custom) this).customSave();

        try {
            final Document doc = this.fieldToDoc();
            if (doc.isEmpty()) return;

            this.manager.collection.updateOne(
                    this.getFilter(),
                    new Document("$set", doc),
                    UPSERT
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow to save the data only if the document don't exist in the DB.
     */
    public void saveIfAbsent () {
        try {
            final Document doc = this.fieldToDoc();
            if (doc.isEmpty()) return;

            this.manager.collection.updateOne(
                    this.getFilter(),
                    new Document("$insert", doc),
                    UPSERT
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow to check if the document exist or not.
     * @return The stat of the existence.
     */
    public boolean exist () {
        return manager.existObject(this.getID());
    }

    /**
     * Allow to remove the document if exist.
     */
    public void delete () {
        manager.remove(this.getID());
    }

    /**
     * Allow to close this instance, and use it in a try-resource.
     */
    public void close () {
        // save the data.
        save();
    }

}
