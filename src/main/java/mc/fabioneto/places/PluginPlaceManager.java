package mc.fabioneto.places;

import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PluginPlaceManager implements PlaceManager {

    private final Plugin plugin;
    private final File dir;
    private final Map<UUID, MemCitizen> cache = new HashMap<>();

    private long ttl = 0;
    private BukkitTask autosave;

    public PluginPlaceManager(Plugin plugin, File dir) {
        Preconditions.checkArgument(dir.isDirectory());

        this.plugin = Objects.requireNonNull(plugin);
        this.dir = dir;
    }

    @Override
    public Citizen getCitizen(UUID uid) {
        return cache.computeIfAbsent(uid, MemCitizen::new);
    }

    @Override
    public void load() {
        for (File file : dir.listFiles((file, name) -> (file.isFile() && name.endsWith(".json")))) {
            UUID uid = extractUID(file);
            MemCitizen ctz = new MemCitizen(uid);

            ctz.load(file);
            cache.put(uid, ctz);
        }

        purge();
    }

    private UUID extractUID(File file) {
        String name = file.getName();

        try {
            return UUID.fromString(name.substring(0, name.length() - 5));
        } catch (IllegalArgumentException e) {
            file.delete();
            return null;
        }
    }

    @Override
    public void autosave(int minutes) {
        if (minutes <= 0) {
            autosave.cancel();
            autosave = null;
            return;
        }

        if (autosave != null) {
            autosave.cancel();
        }

        long ticks = (long) minutes * 60 * 20;

        this.autosave = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::save, ticks, ticks);
    }

    @Override
    public void save() {
        purge();

        for (MemCitizen ctz : cache.values()) {
            if (ctz.modified) {
                ctz.save(newFile(ctz.uid));
            }
        }
    }

    private File newFile(UUID uid) {
        String name = ((uid == null) ? "warps" : uid) + ".json";

        return new File(dir, name);
    }

    private void purge() {
        if (ttl <= 0) return;

        Iterator<UUID> it = cache.keySet().iterator();

        while (it.hasNext()) {
            UUID uid = it.next();

            if (uid == null) continue;

            long time = System.currentTimeMillis() - Bukkit.getOfflinePlayer(uid).getLastSeen();

            if (time < ttl) {
                it.remove();
                newFile(uid).delete();
            }
        }
    }

    @Override
    public long getTimeToLive() {
        return ttl;
    }

    @Override
    public void setTimeToLive(long millis) {
        this.ttl = millis;
    }

    private static class MemPlace implements Place {

        private final MemCitizen owner;
        private final String name;
        private final Location location;

        private boolean closed;

        private MemPlace(MemCitizen owner, String name, Location location, boolean closed) {
            this.owner = owner;
            this.name = name.stripTrailing();
            this.location = location.clone();
            this.closed = closed;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Location getLocation() {
            return location.clone();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void setClosed(boolean closed) {
            if ((this.closed != closed) && (owner.modified = true)) {
                this.closed = closed;
            }
        }

        @Override
        public void tphere(Entity entity) {
            entity.teleport(location);
        }
    }

    private static class MemCitizen implements Citizen {

        private final UUID uid;
        private final Map<String, Place> places = new TreeMap<>();

        private boolean modified;

        private MemCitizen(UUID uid) {
            this.uid = Objects.requireNonNull(uid);
        }

        @Override
        public UUID getUID() {
            return uid;
        }

        @Override
        public Collection<Place> getPlaces() {
            return places.values();
        }

        @Override
        public Place getPlace(String id) {
            return places.get(toKey(id));
        }

        @Override
        public Place createPlace(String name, Location location) {
            Place place = new MemPlace(this, name, location, (uid != null));

            return addPlace(place) && (modified = true) ? place : null;
        }

        @Override
        public boolean removePlace(String id) {
            return (places.remove(toKey(id)) != null) && (modified = true);
        }

        @Override
        public boolean ownsPlace(String id) {
            return places.containsKey(toKey(id));
        }

        private boolean addPlace(Place place) {
            return (places.putIfAbsent(getKey(place), place) == null);
        }

        private String getKey(Place place) {
            return toKey(place.getName());
        }

        private String toKey(String id) {
            return id.toLowerCase(Locale.ENGLISH);
        }

        private void save(File file) {
            try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                writer.beginArray();

                for (Place place : places.values()) {
                    Location loc = place.getLocation();

                    writer.beginObject()
                            .name("name").value(place.getName())

                            .name("location").beginObject()
                            .name("world").value(loc.getWorld().getUID().toString())
                            .name("x").value(loc.getX())
                            .name("y").value(loc.getY())
                            .name("z").value(loc.getZ())
                            .name("yaw").value(loc.getYaw())
                            .name("pitch").value(loc.getPitch())
                            .endObject()

                            .name("Closed").value(place.isClosed())
                            .endObject();
                }

                writer.endArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void load(File file) {
            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                reader.beginArray();

                while (reader.hasNext()) {
                    Place place = nextPlace(reader);

                    if (place != null) {
                        addPlace(place);
                    }
                }

                reader.endArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            modified = false;
        }

        private Place nextPlace(JsonReader reader) throws IOException {
            String name = null;
            Location loc = null;
            Boolean closed = null;

            reader.beginObject();

            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "name" -> name = reader.nextString();
                    case "location" -> loc = nextLocation(reader);
                    case "closed" -> closed = reader.nextBoolean();
                    default -> reader.skipValue();
                }
            }

            reader.endObject();

            if ((name == null) || (loc == null) || (closed == null)) {
                return null;
            }

            return new MemPlace(this, name, loc, closed);
        }

        private Location nextLocation(JsonReader reader) throws IOException {
            World world = null;
            Double x = null;
            Double y = null;
            Double z = null;
            Double yaw = null;
            Double pitch = null;

            reader.beginObject();

            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "world" -> world = nextWorld(reader);
                    case "x" -> x = reader.nextDouble();
                    case "y" -> y = reader.nextDouble();
                    case "z" -> z = reader.nextDouble();
                    case "yaw" -> yaw = reader.nextDouble();
                    case "pitch" -> pitch = reader.nextDouble();
                    default -> reader.skipValue();
                }
            }

            reader.endObject();

            if ((world == null) || (x == null) || (y == null) || (z == null) ||
                    (yaw == null) || (pitch == null)) {
                return null;
            }

            return new Location(world, x, y, z, yaw.floatValue(), pitch.floatValue());
        }

        private World nextWorld(JsonReader reader) throws IOException {
            UUID uid = nextUUID(reader);

            return (uid == null) ? null : Bukkit.getWorld(uid);
        }

        private UUID nextUUID(JsonReader reader) throws IOException {
            try {
                return UUID.fromString(reader.nextString());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
