package fr.i360matt.fastmongo.v2.assistants;


/**
 * This class is used to create Criteria object with simple condition.
 * You can also use the Filters class alternatively.;
 */
public class CriteriaStatic {
    public static Criteria isEquals (final String key, final Object value) {
        return new Criteria().isEquals(key, value);
    }

    public static Criteria isEquals (final String field, final Object... value) {
        return new Criteria().isEquals(field, value);
    }

    public static Criteria isNotEquals (final String key, final Object... value) {
        return new Criteria().isNotEquals(key, value);
    }

    public static Criteria isNotEquals (final String key, final Object value) {
        return new Criteria().isNotEquals(key, value);
    }

    public static Criteria exists (final String key) {
        return new Criteria().exists(key);
    }

    public static Criteria notExists (final String key) {
        return new Criteria().notExists(key);
    }

    public static Criteria muchThan (final String field, double number) {
        return new Criteria().muchThan(field, number);
    }

    public static Criteria muchThanOrEqual (final String field, double number) {
        return new Criteria().muchThanOrEqual(field, number);
    }

    public static Criteria lessThan (final String field, double number) {
        return new Criteria().lessThan(field, number);
    }

    public static Criteria lessThanOrEqual (final String field, double number) {
        return new Criteria().lessThanOrEqual(field, number);
    }

    public static Criteria isNull (final String field) {
        return new Criteria().isNull(field);
    }

    public static Criteria isNotNull (final String field) {
        return new Criteria().isNotNull(field);
    }



}
