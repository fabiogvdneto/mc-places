package me.fabiogvdneto.places.common.repository.java;

import me.fabiogvdneto.places.common.repository.KeyedRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractJavaKeyedRepository<K, V> implements KeyedRepository<K, V> {

    private final Path directory;

    public AbstractJavaKeyedRepository(Path directory) {
        this.directory = Objects.requireNonNull(directory);
    }

    protected abstract K dataToKey(V data);

    protected abstract K filenameToKey(String id);

    @Override
    public void mount() throws IOException {
        Files.createDirectories(directory);
    }

    @Override
    public Collection<K> fetchKeys() throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .map(filename -> filename.substring(0, filename.length() - 4))
                    .map(this::filenameToKey)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    @Override
    public void storeOne(V data) throws IOException {
        select(dataToKey(data)).store(data);
    }

    @Override
    public V fetchOne(K key) throws IOException {
        return select(key).fetch();
    }

    @Override
    public void deleteOne(K key) throws IOException {
        select(key).delete();
    }

    private JavaSingleRepository<V> select(K key) {
        return new JavaSingleRepository<>(directory.resolve(key + ".ser"));
    }

    @Override
    public void delete() throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Nothing we can do to help here.
                }
            });
        }
    }
}
