package fr.i360matt.fastmongo.v2.utils;

import fr.i360matt.fastmongo.v2.utils.objenesis.Objenesis;
import fr.i360matt.fastmongo.v2.utils.objenesis.ObjenesisStd;
import fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Constructor;

/**
 * Read: http://objenesis.org/tutorial.html
 */

public final class Instantiate {
    private static final Objenesis objenesis = new ObjenesisStd(); // or ObjenesisSerializer

    public static <T> T newInstance (final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            try {
                final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                final Object[] args = new Object[constructor.getParameterCount()];
                for (int i = 0; i < constructor.getParameterCount(); i++)
                    args[i] = null;
                return (T) constructor.newInstance(args);
            } catch (Exception e2) {
                // @Deprecated

                ObjectInstantiator<T> instantiator = objenesis.getInstantiatorOf(clazz);;

                return instantiator.newInstance();
            }
        }
    }
}
