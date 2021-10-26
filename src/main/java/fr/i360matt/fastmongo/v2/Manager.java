package fr.i360matt.fastmongo.v2;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import fr.i360matt.fastmongo.v2.annotations.ID;
import fr.i360matt.fastmongo.v2.exceptions.NoDatabaseException;
import fr.i360matt.fastmongo.v2.exceptions.NoIDFieldException;
import fr.i360matt.fastmongo.v2.utils.Instantiate;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a collection in MongoDB.
 *
 * @see Database
 * @see Element
 * @see Sort
 *
 * @author 360matt
 * @version 2.0.0
 */
public class Manager <T extends Element> {

    protected final Class<T> clazz;
    private final String name;

    private Field fieldID;


    private Field[] fieldsCache;
    final Instantiate<T> instantiate;

    protected final Database database;
    protected final MongoCollection<Document> collection;


    public Manager (final Class<T> clazz, final String name, final Database database) {
        this.clazz = clazz;
        this.instantiate = new Instantiate<>(clazz);
        this.initFieldsCache();

        this.name = name;

        this.database = (database != null) ? database : Database.getDefault();
        if (this.database == null) {
            throw new NoDatabaseException();
        }
        if (!this.database.existCollect(name))
            this.database.createCollect(name);
        this.collection = this.database.getCollect(name);
    }

    public Manager (final Class<T> clazz, final String name) {
        this(clazz, name, Database.getDefault());
    }

    /**
     * This method allows fields to be cached by filtering the statics fields
     * and making the rest of the fields accessible.
     */
    protected void initFieldsCache () {
        final List<Field> tmp = new ArrayList<>();
        for (final Field field : this.clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                tmp.add(field);

                ID annotID = field.getAnnotation(ID.class);
                if (annotID != null)
                    fieldID = field;
            }
        }
        this.fieldsCache = tmp.toArray(new Field[0]);

        if (this.fieldID == null) {
            throw new NoIDFieldException();
        }
    }

    protected Field[] getFieldsCache () {
        return this.fieldsCache;
    }




    // _________________________________________________________________________________________________________________


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
    public Field getFieldID () {
        return this.fieldID;
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
        return collection.countDocuments(new Document(this.fieldID.getName(), id)) > 0;
    }

    /**
     * Allows to delete the item
     * @param id element id
     */
    public final void remove (final Object id) {
        collection.deleteOne(Filters.eq(this.fieldID.getName(), id));
    }


    // _________________________________________________________________________________________________________________

    /**
     * Allows to create a classification by one or more fields
     * @param fields chosen fields
     * @return an instance of the ranking module
     */
    public final Sort<T> buildSort (final String... fields) {
        return new Sort<>(this).ascending(fields);
    }

    /**
     * Allows you to create a classification by one or more fields
     * @return an instance of the ranking module
     */
    public final Sort<T> buildSort () {
        return new Sort<>(this);
    }



}
