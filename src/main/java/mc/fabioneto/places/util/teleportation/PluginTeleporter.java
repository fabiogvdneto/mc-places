package mc.fabioneto.places.util.teleportation;

import mc.fabioneto.places.Places;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

public class PluginTeleporter implements Teleporter {

    private final Plugin plugin;
    private final Map<UUID, Teleportation> tasks = new HashMap<>();

    public PluginTeleporter(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Collection<Teleportation> getAll() {
        return Collections.unmodifiableCollection(tasks.values());
    }

    @Override
    public Teleportation get(UUID id) {
        return tasks.get(id);
    }

    @Override
    public Teleportation create(Player player, Location dest) {
        return create(player, dest, 0);
    }

    @Override
    public Teleportation create(Player player, Location dest, int delay) {
        return new PluginTeleportation(player, dest, delay);
    }

    private class PluginTeleportation extends BukkitRunnable implements Teleportation {

        private final Player player;
        private final Location dest;

        private int counter;

        private Consumer<Teleportation> onCountdown = (t -> { });

        private PluginTeleportation(Player player, Location dest, int delay) {
            this.player = Objects.requireNonNull(player);
            this.dest = dest.clone();
            this.counter = delay;
        }

        @Override
        public Player getPlayer() {
            return player;
        }

        @Override
        public Location getDestination() {
            return dest;
        }

        @Override
        public void run() {
            onCountdown.accept(this);

            if (isCancelled()) return;

            if (counter-- == 0) {
                player.teleport(dest);
                cancel();
            }
        }

        @Override
        public void start() throws IllegalStateException {
            Teleportation old = tasks.put(player.getUniqueId(), this);

            if ((old != null) && !old.isCancelled()) {
                old.cancel();
            }

            runTaskTimer(plugin, 0, 20);
        }

        @Override
        public int getCounter() {
            return counter;
        }

        @Override
        public void setCounter(int seconds) {
            this.counter = seconds;
        }

        @Override
        public void updateCounter(int seconds) {
            this.counter += seconds;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();

            tasks.remove(player.getUniqueId());
        }

        @Override
        public void onCountdown(Consumer<Teleportation> callback) {
            this.onCountdown = Objects.requireNonNull(callback);
        }
    }
}
