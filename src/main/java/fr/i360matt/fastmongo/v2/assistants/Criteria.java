package fr.i360matt.fastmongo.v2.assistants;


import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to build a criteria for a query.
 * You can also use the Filters class alternatively.;
 */
public final class Criteria {

    private final List<Bson> or;
    private final List<Bson> filters;

    public Criteria () {
        this.filters = new ArrayList<>();
        this.or = new ArrayList<>();
    }

    public Criteria isEquals (final String field, final Object value) {
        this.filters.add(Filters.eq(field, value));
        return this;
    }

    public Criteria isEquals (final String field, final Object... value) {
        this.filters.add(Filters.all(field, value));
        return this;
    }

    public Criteria isNotEquals (final String field, final Object... value) {
        this.filters.add(Filters.ne(field, value));
        return this;
    }

    public Criteria isNotEquals (final String field, final Object value) {
        this.filters.add(Filters.ne(field, value));
        return this;
    }

    public Criteria exists (final String field) {
        this.filters.add(Filters.exists(field));
        return this;
    }

    public Criteria notExists (final String field) {
        this.filters.add(Filters.not(Filters.exists(field)));
        return this;
    }

    public Criteria and (final Bson filter) {
        this.filters.add(filter);
        return this;
    }

    public Criteria or (final Bson filter) {
        this.or.add(filter);
        return this;
    }

    public Criteria or (final Criteria criteria) {
        this.filters.add(criteria.getBson());
        return this;
    }

    public Criteria muchThan (final String field, double number) {
        this.filters.add(Filters.gt(field, number));
        return this;
    }

    public Criteria muchThanOrEqual (final String field, double number) {
        this.filters.add(Filters.gte(field, number));
        return this;
    }

    public Criteria lessThan (final String field, double number) {
        this.filters.add(Filters.lt(field, number));
        return this;
    }

    public Criteria lessThanOrEqual (final String field, double number) {
        this.filters.add(Filters.lte(field, number));
        return this;
    }

    public Criteria isNull (final String field) {
        this.filters.add(Filters.eq(field, null));
        return this;
    }

    public Criteria isNotNull (final String field) {
        this.filters.add(Filters.ne(field, null));
        return this;
    }




    public Bson getBson () {
        if (!this.or.isEmpty()) {
            final List<Bson> res = new ArrayList<>();
            res.addAll(this.or);
            res.add(Filters.and(this.filters));



            return Filters.or(res);
        }

        return Filters.and(this.filters);
    }
}
