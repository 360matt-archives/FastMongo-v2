package fr.i360matt.fastmongo.v2;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a collection in MongoDB.
 *
 * @see Database
 * @see Structure
 * @see Sort
 *
 * @author 360matt
 * @version 2.0.0
 */
public class Manager {

    protected final Database database;
    protected final MongoCollection<Document> collection;

    private final String name;
    private String fieldID = "_id";

    private Field[] fieldsCache;


    public Manager (final String name) {
        this.name = name;

        if ((this.database = Database.getDefault()) == null) {
            throw new RuntimeException("No default db defined.");
        }

        if (!this.database.existCollect(name))
            this.database.createCollect(name);

        this.collection = this.database.getCollect(name);

    }

    public Manager (final String name, final Database database) {
        this.name = name;
        this.database = database;

        if (!this.database.existCollect(name))
            this.database.createCollect(name);

        this.collection = this.database.getCollect(name);
    }

    /**
     * Allow to retrieve the name of the collection.
     * @return The name of collection.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Allows to retrieve the name of the field which is used as ID in the API.
     * @return The field used to serve as ID.
     */
    public String getFieldID () {
        return this.fieldID;
    }

    /**
     * Allows to define the name of the field which will be used as ID for the API.
     * @param id The field used to serve as ID.
     */
    public void setFieldID (final String id) {
        this.fieldID = id;
    }

    /**
     * This method allows fields to be cached by filtering the statics fields
     * and making the rest of the fields accessible.
     *
     * @param clazz The data class which will be used and which contains all the data fields.
     * @return The correct fields.
     */
    protected Field[] getFieldsCache (final Class<?> clazz) {
        if (this.fieldsCache == null) {
            final List<Field> tmp = new ArrayList<>();
            for (final Field field :clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    tmp.add(field);
                }
            }
            this.fieldsCache = tmp.toArray(new Field[0]);
        }
        return this.fieldsCache;
    }

    /**
     * Used to retrieve the native MongoCollection object from the MongoDB driver.
     * @return native MongoCollection object from the MongoDB driver.
     */
    public MongoCollection<Document> getMongoCollection () {
        return this.collection;
    }

    /**
     * Allows you to completely clear a collection.
     */
    public void drop () {
        this.collection.drop();
    }

    /**
     * Statically allows you to check if a document exists via its field ID.
     * @param id The ID of the document.
     * @return The status of its existence.
     */
    public final boolean existObject (final Object id) {
        return collection.countDocuments(new Document(this.fieldID, id)) > 0;
    }

    /**
     * Allows to delete the item
     * @param id element id
     */
    public final void remove (final Object id) {
        collection.deleteOne(Filters.eq(this.fieldID, id));
    }


    // _________________________________________________________________________________________________________________

    /**
     * Allows to create a classification by one or more fields
     * @param fields chosen fields
     * @return an instance of the ranking module
     */
    public final Sort buildSort (final String... fields) {
        return new Sort(this).ascending(fields);
    }

    /**
     * Allows you to create a classification by one or more fields
     * @return an instance of the ranking module
     */
    public final Sort buildSort () {
        return new Sort(this);
    }



}
