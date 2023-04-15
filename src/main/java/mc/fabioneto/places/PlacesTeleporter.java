package mc.fabioneto.places;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.util.teleportation.PluginTeleporter;
import mc.fabioneto.places.util.teleportation.Teleportation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class PlacesTeleporter extends PluginTeleporter {

    private final PlacesPlugin plugin;

    public PlacesTeleporter(PlacesPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    public void start(Player player, Place place) {
        int delay = computeDelay(player);

        Teleportation teleportation = create(player, place.getLocation(), delay);

        Consumer<Teleportation> callback = (t -> {});

        if (!plugin.getConfig().getBoolean("movement-allowed")) {
            callback = callback.andThen(new MovementDetector(player.getLocation()));
        }

        if (!plugin.getConfig().getBoolean("damage-allowed")) {
            callback = callback.andThen(new DamageDetector());
        }

        teleportation.onCountdown(callback.andThen(new FeedbackProvider(place.getName())));
        teleportation.start();
    }

    private int computeDelay(Player p) {
        int max = plugin.getConfig().getInt("max-delay");

        return IntStream.range(0, max)
                .filter(i -> p.hasPermission("places.delay." + i))
                .findFirst().orElse(max);
    }

    private class MovementDetector implements Consumer<Teleportation> {

        private final int x;
        private final int y;
        private final int z;

        private MovementDetector(Location loc) {
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
        }

        @Override
        public void accept(Teleportation t) {
            Player p = t.getPlayer();
            Location loc = p.getLocation();

            if ((x != loc.getBlockX()) || (y != loc.getBlockY()) || (z != loc.getBlockZ())) {
                t.cancel();
                plugin.getLanguage().translate("teleportation.movement-not-allowed").send(p);
            }
        }
    }

    private class DamageDetector implements Consumer<Teleportation> {

        private double healthTracker;

        @Override
        public void accept(Teleportation t) {
            Player p = t.getPlayer();
            double health = p.getHealth();

            if (health < healthTracker) {
                t.cancel();
                plugin.getLanguage().translate("teleportation.damage-not-allowed").send(p);
                return;
            }

            healthTracker = health;
        }
    }

    private class FeedbackProvider implements Consumer<Teleportation> {

        private final String place;

        public FeedbackProvider(String place) {
            this.place = place;
        }

        @Override
        public void accept(Teleportation t) {
            if (t.isCancelled()) return;

            Player p = t.getPlayer();
            int counter = t.getCounter();

            if (counter == 0) {
                plugin.getLanguage().translate("teleportation.finished").format(place).send(p);
            } else {
                plugin.getLanguage().translate("teleportation.countdown").format(counter).send(p);
            }
        }
    }
}
