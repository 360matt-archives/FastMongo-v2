package fr.i360matt.fastmongo.v2.utils;

import java.io.*;
import java.util.Base64;

/**
 * Serialize and deserialize objects to and from Base64.
 */
public final class SerializeBase64 {

    public static <T> String serialize (T instance) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeUnshared(instance);

                byte[] bytes = baos.toByteArray();
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T deserialize (final String string) {
        byte[] decoded = Base64.getDecoder().decode(string);

        try (final ByteArrayInputStream bais = new ByteArrayInputStream(decoded)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (T) ois.readUnshared();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
