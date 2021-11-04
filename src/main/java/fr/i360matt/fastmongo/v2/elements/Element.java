package fr.i360matt.fastmongo.v2.elements;

import com.mongodb.client.model.UpdateOptions;
import fr.i360matt.fastmongo.v2.Manager;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Closeable;
import java.util.Map;

/**
 * This class is used to load document data from MongoDB, manipulate it and then save it in the DB.
 *
 * @author 360matt
 * @version 2.0.0
 */
public class Element extends StorableObject implements Closeable {
    protected static final UpdateOptions UPSERT = new UpdateOptions().upsert(true);

    protected Manager<?> manager;

    private Document filter;
    private Object cache_id;

    public Element (final Manager<?> manager) {
        this.manager = manager;
        this.cache = manager.getCache();
    }

    protected final Document getFilter () {
        if (this.filter == null)
            this.filter = new Document(this.manager.getFieldID().getName(), getID());
        return this.filter;
    }

    public Object getID () {
        if (this.cache_id == null) {
            try {
                this.cache_id = this.getCache().getFieldID().get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this.cache_id;
    }

    public final Manager<?> getManager () {
        return manager;
    }

    public final void setManager (final Manager<?> manager) {
        this.manager = manager;
        this.cache = manager.getCache();
    }

    // ____________________________________________________________________________________________



    /**
     * Allows to load the data by custom criteria.
     */
    public final void load (final Bson bson) {
        final Document doc = manager.getMongoCollection().find(bson).first();
        if (doc != null) {
            this.deserialize(doc);
        }
    }

    /**
     * Allows to load the data.
     */
    public final void load () {
        final Document doc = this.getFilter();
        this.load(doc);
    }

    /**
     * Allow to save the data.
     */
    public final void save () {
        try {
            final Map<String, Object> doc = this.serialize();
            if (doc.isEmpty()) return;

            this.manager.getMongoCollection().updateOne(
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
    public final void saveIfAbsent () {
        try {
            final Map<String, Object> doc = this.serialize();
            if (doc.isEmpty()) return;

            this.manager.getMongoCollection().updateOne(
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
    public final boolean exist () {
        return manager.exist(this.getID());
    }

    /**
     * Allow to remove the document if exist.
     */
    public final void delete () {
        manager.remove(this.getID());
    }

    /**
     * Allow to close this instance, and use it in a try-resource.
     */
    public final void close () {
        // save the data.
        save();
    }

}
