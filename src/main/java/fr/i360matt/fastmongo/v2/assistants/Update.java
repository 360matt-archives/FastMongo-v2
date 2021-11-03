package fr.i360matt.fastmongo.v2.assistants;

import org.bson.Document;

/**
 * This class is used to update one or multiple documents in a collection.
 */
public class Update {
    private final Document actions;

    public Update () {
        this.actions = new Document();
    }

    public Document getActions () {
        return this.actions;
    }

    private Document getOrGenAction (final String key) {
        Document actions = (Document) this.actions.getOrDefault(key, null);
        if (actions == null) {
            actions = new Document();
            this.actions.put(key, actions);
        }
        return actions;
    }



    public Update increment (String field, int value) {
        this.getOrGenAction("$inc").put(field, value);
        return this;
    }

    public Update decrement (String field, int value) {
        this.getOrGenAction("$decr").put(field, value);
        return this;
    }

    public Update set (String field, Object value) {
        this.getOrGenAction("$set").put(field, value);
        return this;
    }

    public Update setOnInsert (String field, Object value) {
        this.getOrGenAction("$setOnInsert").put(field, value);
        return this;
    }

    public Update unset (String field) {
        this.getOrGenAction("$unset").put(field, 1);
        return this;
    }

    public Update addToArray (String field, Object value) {
        this.getOrGenAction("$push").put(field, value);
        return this;
    }

    public Update removeFromArray (String field, Object value) {
        this.getOrGenAction("$pull").put(field, value);
        return this;
    }

    public Update removeFirstFromArray (String field) {
        this.getOrGenAction("$pop").put(field, 1);
        return this;
    }

    public Update removeLastFromArray (String field) {
        this.getOrGenAction("$pop").put(field, -1);
        return this;
    }

    public Update addToArrayIfAbsent (String field, Object value) {
        this.getOrGenAction("$addToSet").put(field, value);
        return this;
    }

    public Update delete (String field) {
        this.getOrGenAction("$delete").put(field, 1);
        return this;
    }

    public Update rename (String field, String newField) {
        this.getOrGenAction("$rename").put(field, newField);
        return this;
    }

    public Update min (String field, Number value) {
        this.getOrGenAction("$min").put(field, value);
        return this;
    }

    public Update max (String field, Number value) {
        this.getOrGenAction("$max").put(field, value);
        return this;
    }

    public Update multiply (String field, Number value) {
        this.getOrGenAction("$mul").put(field, value);
        return this;
    }

    public Update divide (String field, Number value) {
        this.getOrGenAction("$divide").put(field, value);
        return this;
    }

    public Update modulo (String field, Number value) {
        this.getOrGenAction("$mod").put(field, value);
        return this;
    }

    public Update bitwiseAnd (String field, Number value) {
        this.getOrGenAction("$bit").append(field, new Document("$and", value));
        return this;
    }

    public Update bitwiseOr (String field, Number value) {
        this.getOrGenAction("$bit").append(field, new Document("$or", value));
        return this;
    }

    public Update bitwiseXor (String field, Number value) {
        this.getOrGenAction("$bit").append(field, new Document("$xor", value));
        return this;
    }



}
