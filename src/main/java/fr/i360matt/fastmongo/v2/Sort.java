package fr.i360matt.fastmongo.v2;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is used to classify a Document in relation to one or more fields
 *
 * @author 360matt
 * @version 2.0.0
 */
public final class Sort implements Closeable {
    public enum Direction {
        ASCENDING, DESCENDING
    }

    private FindIterable<Document> iter;
    private final Manager manager;
    private int limit;
    private int skip;

    /**
     * Allows to create a classification that can be completed later
     * @param manager The manager of the collection where the classification is supposed to take place
     */
    public Sort (final Manager manager) {
        this.manager = manager;
    }

    /**
     * Allows to define the maximum number of results allowed at the output
     * @param limit The number of documents to classify
     * @return The current instance
     */
    public final Sort setLimit (final int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Allows to define the number of documents to skip before sorting the documents.
     * @param skip The number of documents to skip before sorting the documents.
     * @return The current instance
     */
    public final Sort skip (final int skip) {
        this.skip = skip;
        return this;
    }

    private Sort addRule (final boolean ascending, final String... fields) {
        final Bson choice = (ascending) ? Sorts.ascending(fields) : Sorts.descending(fields);
        this.iter = (this.iter == null) ? manager.collection.find().sort(choice) : this.iter.sort(choice);
        return this;
    }

    /**
     * Allows to add a ascending ranking rule
     * @param fields The fields you want to add as criteria
     * @return The current instance
     */
    public final Sort ascending (final String... fields) {
        return addRule(true, fields);
    }

    /**
     * Allows to add a descending ranking rule
     * @param fields The fields you want to add as criteria
     * @return The current instance
     */
    public final Sort descending (final String... fields) {
        return addRule(false, fields);
    }



    // _________________________________________________________________________________________________________________

    /**
     * Allow to retrieve the native Iterable object of the Mongodb driver.
     * @return The native Iterable object of the Mongodb driver.
     */
    public final FindIterable<Document> getIterable () {
        if (this.limit > 0) this.iter.limit(this.limit);
        if (this.skip > 0) this.iter.skip(this.skip);

        return this.iter;
    }

    /**
     * Allows you to retrieve the result in the form of a Document list
     * @return the list of Document.
     */
    public final List<Document> getDocuments () {
        final List<Document> res = new ArrayList<>();
        this.getIterable().iterator().forEachRemaining(res::add);
        this.close();
        return res;
    }

    /**
     * Allows to retrieve objects in the form of a data instance.
     * @return List of data instances.
     */
    public final <T extends Structure> List<T> getStructure (final Supplier<T> init) {
        final List<T> res = new ArrayList<>();
        this.getIterable().iterator().forEachRemaining(x -> {
            final T inst = init.get();
            try {
                inst.docToField(x);
                res.add(inst);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        this.close();
        return res;
    }

    /**
     * Allows to retrieve objects in the form of a data instance in a consumer.
     */
    public final <T extends Structure> void foreachStructure (final Supplier<T> init, final Consumer<T> consumer) {
        this.getIterable().iterator().forEachRemaining(x -> {
            final T inst = init.get();
            try {
                inst.docToField(x);
                consumer.accept(inst);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        this.close();
    }

    public void close () {
        this.iter = null;
    }

}