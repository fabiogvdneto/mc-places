package com.github.fabiogvdneto.places.repository.java;

import com.github.fabiogvdneto.places.common.repository.java.AbstractJavaKeyedRepository;
import com.github.fabiogvdneto.places.repository.UserRepository;
import com.github.fabiogvdneto.places.repository.data.UserData;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class JavaUserRepository extends AbstractJavaKeyedRepository<UUID, UserData> implements UserRepository {

    public JavaUserRepository(Path dir) {
        super(dir);
    }

    @Override
    protected UUID getKey(UserData data) {
        return data.uid();
    }

    @Override
    protected UUID getKeyFromString(String id) {
        return UUID.fromString(id);
    }

    @Override
    public int purge(int days) throws IOException {
        int purgeCount = 0;
        Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);

        for (UUID uid : fetchKeys()) {
            long lastSeen = Bukkit.getOfflinePlayer(uid).getLastSeen();

            if (lastSeen > 0 && Instant.ofEpochMilli(lastSeen).isBefore(limit)) {
                try {
                    deleteOne(uid);
                    purgeCount++;
                } catch (IOException e) {
                    // Nothing we can do to help here.
                }
            }
        }

        return purgeCount;
    }
}
