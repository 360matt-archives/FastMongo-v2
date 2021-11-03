package fr.i360matt.fastmongo.v2.assistants;

/**
 * This class is used to create Update object with simple action.
 */
public class UpdateStatic {

    public static Update increment (String field, int value) {
        return new Update().increment(field, value);
    }

    public static Update decrement (String field, int value) {
        return new Update().decrement(field, value);
    }

    public static Update set (String field, Object value) {
        return new Update().set(field, value);
    }

    public static Update setOnInsert (String field, Object value) {
        return new Update().setOnInsert(field, value);
    }

    public static Update unset (String field) {
        return new Update().unset(field);
    }

    public static Update addToArray (String field, Object value) {
        return new Update().addToArray(field, value);
    }

    public static Update removeFromArray (String field, Object value) {
        return new Update().removeFromArray(field, value);
    }

    public static Update removeFirstFromArray (String field) {
        return new Update().removeFirstFromArray(field);
    }

    public static Update removeLastFromArray (String field) {
        return new Update().removeLastFromArray(field);
    }

    public static Update addToArrayIfAbsent (String field, Object value) {
        return new Update().addToArrayIfAbsent(field, value);
    }

    public static Update delete (String field) {
        return new Update().delete(field);
    }

    public static Update rename (String field, String newField) {
        return new Update().rename(field, newField);
    }

    public static Update min (String field, Number value) {
        return new Update().min(field, value);
    }

    public static Update max (String field, Number value) {
        return new Update().max(field, value);
    }

    public static Update multiply (String field, Number value) {
        return new Update().multiply(field, value);
    }

    public static Update divide (String field, Number value) {
        return new Update().divide(field, value);
    }

    public static Update modulo (String field, Number value) {
        return new Update().modulo(field, value);
    }

    public static Update bitwiseAnd (String field, Number value) {
        return new Update().bitwiseAnd(field, value);
    }

    public static Update bitwiseOr (String field, Number value) {
        return new Update().bitwiseOr(field, value);
    }

    public static Update bitwiseXor (String field, Number value) {
        return new Update().bitwiseXor(field, value);
    }



}
