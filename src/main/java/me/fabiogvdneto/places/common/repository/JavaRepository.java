package me.fabiogvdneto.places.common.repository;

import java.io.*;

public abstract class JavaRepository<V> implements SingleRepository<V> {

    private final File file;

    public JavaRepository(File file) {
        this.file = file;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V fetch() throws IOException {
        if (!file.exists()) return createDefault();

        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
            return (V) stream.readObject();
        } catch (ClassNotFoundException e) {
            // Logging (this should never happen)...
        }

        return null;
    }

    @Override
    public void store(V data) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
            stream.writeObject(data);
        }
    }

    @Override
    public void delete() {
        file.delete();
    }

    public abstract V createDefault();

    @Override
    public String toString() {
        return """
                type:java-object-serialization
                location:%s
                """.formatted(file.getPath());
    }
}
