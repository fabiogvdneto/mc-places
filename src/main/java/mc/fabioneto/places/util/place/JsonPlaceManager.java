package mc.fabioneto.places.util.place;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonPlaceManager implements PlaceManager {

    private final Map<UUID, MemPlaceContainer> cache = new HashMap<>();

    private long ttl = 0;

    @Override
    public PlaceContainer getContainer(UUID uid) {
        return cache.computeIfAbsent(uid, MemPlaceContainer::new);
    }

    @Override
    public void load(File dir) {
        File[] files = dir.listFiles(File::isFile);

        if (files == null) return;

        for (File file : files) {
            String filename = file.getName();
            UUID uid = null;

            try {
                uid = UUID.fromString(filename.substring(0, filename.length() - 5));
            } catch (IllegalArgumentException e) {
                if (!filename.equals("global.json")) {
                    file.delete();
                    continue;
                }
            }

            MemPlaceContainer container = new MemPlaceContainer(uid);

            if (container.isOutdated(ttl)) {
                file.delete();
                continue;
            }

            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                container.load(reader);
                cache.put(uid, container);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save(File dir) {
        Iterator<MemPlaceContainer> it = cache.values().iterator();

        while (it.hasNext()) {
            MemPlaceContainer container = it.next();
            File file = container.createFile(dir);

            if (container.isOutdated(ttl)) {
                it.remove();
                file.delete();
            } else if (container.modified) {
                try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                    container.save(writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        private final MemPlaceContainer container;
        private final String name;
        private final Location location;

        private boolean closed;

        private MemPlace(MemPlaceContainer container, String name, Location location, boolean closed) {
            this.container = container;
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
            if ((this.closed != closed) && (container.modified = true)) {
                this.closed = closed;
            }
        }

        @Override
        public void tphere(Entity entity) {
            entity.teleport(location);
        }
    }

    private static class MemPlaceContainer implements PlaceContainer {

        private final UUID uid;
        private final Map<String, Place> places = new TreeMap<>();

        private boolean modified;

        private MemPlaceContainer(UUID uid) {
            this.uid = uid;
        }

        @Override
        public UUID getOwner() {
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

        private boolean isOutdated(long ttl) {
            if ((uid == null) || (ttl <= 0)) return false;

            long time = System.currentTimeMillis() - Bukkit.getOfflinePlayer(uid).getLastSeen();

            return (time > ttl);
        }

        private File createFile(File dir) {
            String name = ((uid == null) ? "global" : uid) + ".json";

            return new File(dir, name);
        }

        private void save(JsonWriter writer) throws IOException {
            writer.beginArray();

            for (Place place : places.values()) {
                Location loc = place.getLocation();

                writer.beginObject()
                        .name("name").value(place.getName())

                        .name("loc").beginObject()
                        .name("world").value(loc.getWorld().getUID().toString())
                        .name("x").value(loc.getX())
                        .name("y").value(loc.getY())
                        .name("z").value(loc.getZ())
                        .name("yaw").value(loc.getYaw())
                        .name("pitch").value(loc.getPitch())
                        .endObject()

                        .name("closed").value(place.isClosed())
                        .endObject();
            }

            writer.endArray();

            modified = false;
        }

        private void load(JsonReader reader) throws IOException {
            reader.beginArray();

            while (reader.hasNext()) {
                Place place = nextPlace(reader);

                if (place != null) {
                    addPlace(place);
                }
            }

            reader.endArray();

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
                    case "loc" -> loc = nextLocation(reader);
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

            if ((world == null) || (x == null) || (y == null) || (z == null)) {
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
