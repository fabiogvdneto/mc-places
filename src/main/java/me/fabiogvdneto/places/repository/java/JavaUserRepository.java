package me.fabiogvdneto.places.repository.java;

import me.fabiogvdneto.places.common.repository.java.AbstractJavaKeyedRepository;
import me.fabiogvdneto.places.repository.UserRepository;
import me.fabiogvdneto.places.repository.data.UserData;
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
    protected UUID dataToKey(UserData data) {
        return data.uid();
    }

    @Override
    protected UUID filenameToKey(String id) {
        return UUID.fromString(id);
    }

    @Override
    public void purge(int days) throws IOException {
        Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);

        for (UUID uid : fetchKeys()) {
            long lastSeen = Bukkit.getOfflinePlayer(uid).getLastSeen();

            if (lastSeen > 0 && Instant.ofEpochMilli(lastSeen).isBefore(limit)) {
                deleteOne(uid);
            }
        }
    }
}
