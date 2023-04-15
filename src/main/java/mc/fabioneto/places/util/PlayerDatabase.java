package mc.fabioneto.places.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerDatabase implements Listener {

    private final Logger logger;
    private final BiMap<UUID, String> cache;

    public PlayerDatabase(Logger logger) {
        this.logger = Objects.requireNonNull(logger);
        this.cache = HashBiMap.create();
    }

    public BiMap<UUID, String> getCache() {
        return cache;
    }

    public UUID fetchID(String name) {
        return cache.inverse().get(name);
    }

    public String fetchName(UUID id) {
        return cache.get(id);
    }

    public void save(File file) {
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            writer.beginObject();

            for (Map.Entry<UUID, String> entry : cache.entrySet()) {
                writer.name(entry.getKey().toString()).value(entry.getValue());
            }

            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File file) {
        if (!file.exists()) return;

        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            reader.beginObject();

            while (reader.hasNext()) {
                UUID uid = toUUID(reader.nextName());

                if (uid == null) {
                    reader.skipValue();
                    return;
                }

                cache.forcePut(uid, reader.nextString());
            }

            reader.endObject();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private UUID toUUID(String serialized) {
        try {
            return UUID.fromString(serialized);
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to convert the following string to uuid: " + serialized);
            return null;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        cache.forcePut(p.getUniqueId(), p.getName());
    }
}
