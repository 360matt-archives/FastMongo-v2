package fr.i360matt.fastmongo.v2;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import fr.i360matt.fastmongo.v2.assistants.Sort;
import fr.i360matt.fastmongo.v2.elements.Element;
import fr.i360matt.fastmongo.v2.exceptions.NoDatabaseException;
import fr.i360matt.fastmongo.v2.exceptions.NoIDFieldException;
import fr.i360matt.fastmongo.v2.utils.Cache;
import fr.i360matt.fastmongo.v2.assistants.Criteria;
import fr.i360matt.fastmongo.v2.utils.Instantiate;
import fr.i360matt.fastmongo.v2.assistants.Update;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

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

    private final Class<T> clazz;
    private final String name;

    private final Cache cache;

    private final Database database;
    private final MongoCollection<Document> collection;


    public Manager (final Class<T> clazz, final String name, final Database database) {
        this.clazz = clazz;
        this.name = name;

        this.cache = new Cache(clazz);
        if (this.cache.getFieldID() == null)
            throw new NoIDFieldException();

        this.database = (database != null) ? database : Database.getDefault();
        if (this.database == null) {
            throw new NoDatabaseException();
        }
        if (!this.database.existCollection(name))
            this.database.createCollection(name);
        this.collection = this.database.getCollection(name);
    }

    public Manager (final Class<T> clazz, final String name) {
        this(clazz, name, Database.getDefault());
    }

    public Manager (final Class<T> clazz, final Database database) {
        this(clazz, clazz.getName(), database);
    }

    public Manager (final Class<T> clazz) {
        this(clazz, clazz.getName(), Database.getDefault());
    }



    // _________________________________________________________________________________________________________________

    public Cache getCache () {
        return this.cache;
    }


    public Class<T> getElementType () {
        return this.clazz;
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
    public Field getFieldID () {
        return this.cache.getFieldID();
    }


    // _________________________________________________________________________________________________________________



    /**
     * Used to retrieve the native MongoCollection object from the MongoDB driver.
     * @return native MongoCollection object from the MongoDB driver.
     */
    public MongoCollection<Document> getMongoCollection () {
        return this.collection;
    }

    /**
     * Used to retrieve the Database object.
     * @return native Database object.
     */
    public Database getDatabase () {
        return this.database;
    }

    /**
     * Allows you to completely clear a collection.
     */
    public void drop () {
        this.collection.drop();
    }


    public void updateOne (Bson filter, Bson update) {
        this.collection.updateOne(filter, update);
    }

    public void updateOne (Criteria criteria, Update update) {
        this.collection.updateOne(criteria.getBson(), update.getActions());
    }

    public void updateMany (Bson filter, Bson update) {
        this.collection.updateMany(filter, update);
    }

    public void updateMany (Criteria criteria, Update update) {
        this.collection.updateMany(criteria.getBson(), update.getActions());
    }


    public T getOne (final Object id) {
        try {
            T instance = Instantiate.newInstance(this.clazz);
            instance.setManager(this);
            this.getFieldID().set(instance, id);
            return instance;
        } catch (final java.lang.IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public T getOne (final Map<String, Object> obj) {
        T instance = Instantiate.newInstance(this.clazz);
        instance.setManager(this);
        instance.load(new Document(obj));
        return instance;
    }

    public T getOne (final Criteria criteria) {
        T instance = Instantiate.newInstance(this.clazz);
        instance.setManager(this);
        instance.load(criteria.getBson());
        return instance;
    }


    private Collection<T> getAsCollection (final Bson obj, final Collection<T> init) {
        return this.getAsCollection(obj, init, null);
    }

    private Collection<T> getAsCollection (final Bson obj, final Collection<T> init, final Consumer<Sort<T>> consumer) {
        final FindIterable<Document> iterable = (obj != null) ? this.collection.find(obj) : this.collection.find();

        if (consumer != null)
            consumer.accept(new Sort<>(this, iterable));

        try (final MongoCursor<Document> iter = iterable.iterator()) {
            while (iter.hasNext()) {
                try {
                    final Document document = iter.next();

                    final T instance = Instantiate.newInstance(this.clazz);
                    instance.setManager(this);
                    instance.deserialize(document);

                    init.add(instance);
                } catch (final java.lang.IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return init;
    }

    public List<T> getAsList () {
        return (List<T>) this.getAsCollection(null, new ArrayList<>());
    }

    public List<T> getAsList (final Map<String, Object> obj) {
        return (List<T>) this.getAsCollection(new Document(obj), new ArrayList<>());
    }

    public List<T> getAsList (final Criteria criteria) {
        return (List<T>) this.getAsCollection(criteria.getBson(), new ArrayList<>());
    }

    public List<T> getAsList (final Consumer<Sort<T>> consumer) {
        return (List<T>) this.getAsCollection(null, new ArrayList<>(), consumer);
    }

    public List<T> getAsList (final Map<String, Object> obj, final Consumer<Sort<T>> consumer) {
        return (List<T>) this.getAsCollection(new Document(obj), new ArrayList<>(), consumer);
    }

    public List<T> getAsList (final Criteria criteria, final Consumer<Sort<T>> consumer) {
        return (List<T>) this.getAsCollection(criteria.getBson(), new ArrayList<>(), consumer);
    }




    public Set<T> getAsSet () {
        return (Set<T>) this.getAsCollection(null, new HashSet<>());
    }

    public Set<T> getAsSet (final Map<String, Object> obj) {
        return (Set<T>) this.getAsCollection(new Document(obj), new HashSet<>());
    }

    public Set<T> getAsSet (final Criteria criteria) {
        return (Set<T>) this.getAsCollection(criteria.getBson(), new HashSet<>());
    }

    public Set<T> getAsSet (final Consumer<Sort<T>> consumer) {
        return (Set<T>) this.getAsCollection(null, new HashSet<>(), consumer);
    }

    public Set<T> getAsSet (final Map<String, Object> obj, final Consumer<Sort<T>> consumer) {
        return (Set<T>) this.getAsCollection(new Document(obj), new HashSet<>(), consumer);
    }

    public Set<T> getAsSet (final Criteria criteria, final Consumer<Sort<T>> consumer) {
        return (Set<T>) this.getAsCollection(criteria.getBson(), new HashSet<>(), consumer);
    }




    /**
     * Statically allows you to check if a document exists via its field ID.
     * @param id The ID of the document.
     * @return The status of its existence.
     */
    public final boolean exist (final Object id) {
        return collection.countDocuments(new Document(this.getFieldID().getName(), id)) > 0;
    }

    /**
     * Statically allows you to check if a document exists via criteria.
     * @param criteria The criteria.
     * @return The status of its existence.
     */
    public final boolean exist (final Criteria criteria) {
        return collection.countDocuments(criteria.getBson()) > 0;
    }

    public final int count (final Criteria criteria) {
        return (int) collection.countDocuments(criteria.getBson());
    }

    /**
     * Allows to delete the item by id
     * @param id element id
     */
    public final void remove (final Object id) {
        collection.deleteOne(Filters.eq(this.getFieldID().getName(), id));
    }

    /**
     * Allows to delete the item by document
     * @param document the criteria as document
     */
    public final void removeOne (final Document document) {
        collection.deleteOne(document);
    }

    /**
     * Allows to delete the item by Criteria
     * @param criteria the criteria
     */
    public final void removeOne (final Criteria criteria) {
        collection.deleteOne(criteria.getBson());
    }

    /**
     * Allows to delete multiple item by document
     * @param document the criteria as document
     */
    public final void removeMany (final Document document) {
        collection.deleteMany(document);
    }

    /**
     * Allows to delete multiple item by Criteria
     * @param criteria the criteria
     */
    public final void removeMany (final Criteria criteria) {
        collection.deleteMany(criteria.getBson());
    }


    // _________________________________________________________________________________________________________________


}
