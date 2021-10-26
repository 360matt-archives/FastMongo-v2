package fr.i360matt.fastmongo.v2.exceptions;

public class NoDatabaseException extends RuntimeException {
    public NoDatabaseException () {
        super("No default db defined.");
    }
}
