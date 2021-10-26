package fr.i360matt.fastmongo.v2.exceptions;

public class NoIDFieldException extends RuntimeException {
    public NoIDFieldException () {
        super("A field must have an ID annotation.");
    }
}
