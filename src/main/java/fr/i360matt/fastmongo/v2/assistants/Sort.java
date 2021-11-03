package fr.i360matt.fastmongo.v2.assistants;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import fr.i360matt.fastmongo.v2.Manager;
import fr.i360matt.fastmongo.v2.elements.Element;
import fr.i360matt.fastmongo.v2.utils.Instantiate;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is used to classify a Document in relation to one or more fields
 *
 * @author 360matt
 * @version 2.0.0
 */
public final class Sort<T extends Element> {

    public enum Direction {
        ASCENDING, DESCENDING
    }

    private final FindIterable<Document> iter;
    private final Manager<T> manager;

    /**
     * Allows to create a classification that can be completed later
     * @param manager The manager of the collection where the classification is supposed to take place
     */
    public Sort (final Manager<T> manager, final FindIterable<Document> iter) {
        this.manager = manager;
        this.iter = iter;
    }

    /**
     * Allows to define the maximum number of results allowed at the output
     * @param limit The number of documents to classify
     * @return The current instance
     */
    public Sort<T> setLimit (final int limit) {
        this.iter.limit(limit);
        return this;
    }

    /**
     * Allows to define the number of documents to skip before sorting the documents.
     * @param skip The number of documents to skip before sorting the documents.
     * @return The current instance
     */
    public Sort<T> skip (final int skip) {
        this.iter.skip(skip);
        return this;
    }

    private Sort<T> addRule (final boolean ascending, final String... fields) {
        final Bson choice = (ascending) ? Sorts.ascending(fields) : Sorts.descending(fields);
        this.iter.sort(choice);
        return this;
    }

    /**
     * Allows to add a ascending ranking rule
     * @param fields The fields you want to add as criteria
     * @return The current instance
     */
    public Sort<T> ascending (final String... fields) {
        return addRule(true, fields);
    }

    /**
     * Allows to add a descending ranking rule
     * @param fields The fields you want to add as criteria
     * @return The current instance
     */
    public Sort<T> descending (final String... fields) {
        return addRule(false, fields);
    }



    // _________________________________________________________________________________________________________________

    /**
     * Allow to retrieve the native Iterable object of the Mongodb driver.
     * @return The native Iterable object of the Mongodb driver.
     */
    public FindIterable<Document> getIterable () {
        return this.iter;
    }

    /**
     * Allows you to retrieve the result in the form of a Document list
     * @return the list of Document.
     */
    public List<Document> getDocuments () {
        final List<Document> res = new ArrayList<>();
        this.getIterable().iterator().forEachRemaining(res::add);
        return res;
    }

    /**
     * Allows to retrieve objects in the form of a data instance.
     * @return List of data instances.
     */
    public List<T> getElements () {
        final List<T> res = new ArrayList<>();
        this.getIterable().iterator().forEachRemaining(x -> {
            try {
                final T inst = Instantiate.newInstance(this.manager.getElementType());
                inst.setManager(this.manager);
                inst.deserialize(x);
                res.add(inst);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return res;
    }

    /**
     * Allows to retrieve objects in the form of a data instance in a consumer.
     */
    public void foreachElements (final Consumer<T> consumer) {
        this.getIterable().iterator().forEachRemaining(x -> {
            try {
                final T inst = Instantiate.newInstance(this.manager.getElementType());
                inst.setManager(this.manager);
                inst.deserialize(x);
                consumer.accept(inst);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}