package me.fabiogvdneto.places.repository.java;

import me.fabiogvdneto.places.common.repository.JavaRepository;
import me.fabiogvdneto.places.common.repository.SingleRepository;
import me.fabiogvdneto.places.repository.UserRepository;
import me.fabiogvdneto.places.repository.data.UserData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaUserRepository implements UserRepository {

    private final File folder;

    public JavaUserRepository(File folder) throws IllegalArgumentException {
        this.folder = folder;

        if (!folder.isDirectory() && !folder.mkdirs())
            throw new IllegalArgumentException("the given folder is not a directory");
    }

    @Override
    public String toString() {
        return """
                type:java-object-serialization
                location:%s
                """
                .formatted(folder.getPath());
    }

    @Override
    public Collection<UUID> keys() {
        String[] files = folder.list((file, name) -> file.isFile() && name.endsWith(".json"));

        if (files == null) return Collections.emptySet();

        return Arrays.stream(files)
                .map(name -> name.substring(0, name.length() - 5))
                .map(name -> {
                    try {
                        return UUID.fromString(name);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public SingleRepository<UserData> select(UUID key) {
        File file = new File(folder, key + ".json");

        return new JavaRepository<>(file) {
            @Override
            public UserData createDefault() {
                return new UserData(key, Collections.emptyList());
            }
        };
    }

    @Override
    public void purge(int days) throws IOException {
        Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);

        try (Stream<Path> stream = Files.list(folder.toPath())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().endsWith(".json"))
                    .filter(path -> {
                        try {
                            // Select old files to purge.
                            return Files.getLastModifiedTime(path).toInstant().isBefore(limit);
                        } catch (IOException e) {
                            // Should not happen...
                            return false;
                        }
                    }).forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Should not happen...
                        }
                    });
        }
    }

    @Override
    public void delete() {
        // delete all files...
    }
}
