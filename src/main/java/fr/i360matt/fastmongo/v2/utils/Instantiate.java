package fr.i360matt.fastmongo.v2.utils;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 *
 * Read: http://objenesis.org/tutorial.html
 *
 * @param <T> the class to instantiate.
 */

public class Instantiate <T> {
    protected static final Objenesis objenesis = new ObjenesisStd(); // or ObjenesisSerializer

    private final ObjectInstantiator<T> instantiator;

    public Instantiate (final Class<T> clazz) {
        instantiator = objenesis.getInstantiatorOf(clazz);
    }

    public T newInstance () {
        return instantiator.newInstance();
    }
}
