package me.fabiogvdneto.places.common.teleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PluginTeleporter implements Teleporter {

    private final Plugin owner;
    private final Map<UUID, Teleportation> ongoing = new HashMap<>();
    private final Map<UUID, Location> previous = new HashMap<>();
    private CommandBlocker commandBlocker;

    public PluginTeleporter(Plugin owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    @Override
    public Collection<Teleportation> ongoing() {
        return Collections.unmodifiableCollection(ongoing.values());
    }

    @Override
    public Teleportation ongoing(Player recipient) {
        return ongoing.get(recipient.getUniqueId());
    }

    @Override
    public Teleportation create(Player recipient, Location dest) {
        return create(recipient, () -> dest);
    }

    @Override
    public Teleportation create(Player recipient, Supplier<Location> dest) {
        return new TeleportationImpl(recipient, dest);
    }

    @Override
    public Teleportation back(Player recipient) {
        Location destination = previous.get(recipient.getUniqueId());
        return (destination == null) ? null : create(recipient, destination);
    }

    public void registerCommandListener(Predicate<String> filter) {
        owner.getServer().getPluginManager().registerEvents(new CommandBlocker(ongoing.keySet(), filter), owner);
    }

    public void unregisterCommandListener() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(commandBlocker);
        commandBlocker = null;
    }

    private class TeleportationImpl extends BukkitRunnable implements Teleportation {

        private final Player recipient;
        private final Supplier<Location> destination;
        private final List<Consumer<Teleportation>> observers = new LinkedList<>();

        private int delay;
        private int counter;

        private TeleportationImpl(Player recipient, Supplier<Location> dest) {
            this.recipient = Objects.requireNonNull(recipient);
            this.destination = Objects.requireNonNull(dest);
            this.delay = 0;
            // A negative value indicates that this teleportation has not yet started or has already ended.
            this.counter = -1;
        }

        @Override
        public Player getRecipient() {
            return recipient;
        }

        @Override
        public Location getDestination() {
            return destination.get();
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public int getCounter() {
            return counter;
        }

        @Override
        public void begin() throws IllegalStateException {
            this.counter = delay;

            if (counter > 0) {
                register();
                runTaskTimer(owner, 0L, 20L);
            } else {
                updateObservers(false);
                tpnow();
                this.counter = -1;
            }
        }

        @Override
        public void cancel() throws IllegalStateException {
            super.cancel();
            unregister();
        }

        @Override
        public Teleportation withDelay(int delay) {
            this.delay = Math.max(0, delay);
            return this;
        }

        @Override
        public Teleportation onCountdown(Consumer<Teleportation> callback) {
            observers.add(callback);
            return this;
        }

        @Override
        public void run() {
            updateObservers(true);

            if (counter-- == 0) {
                cancel();
                tpnow();
            }
        }

        private void updateObservers(boolean scheduled) {
            for (Consumer<Teleportation> callback : observers) {
                callback.accept(this);

                // Ensure the task was scheduled before checking if it was cancelled.
                if (scheduled && isCancelled()) return;
            }
        }

        private void tpnow() {
            Location location = destination.get();

            if (location != null) {
                previous.put(recipient.getUniqueId(), recipient.getLocation());
                recipient.teleport(location);
            }
        }

        private void register() {
            Teleportation previous = ongoing.put(recipient.getUniqueId(), this);

            if (previous != null) {
                previous.cancel();
            }
        }

        private void unregister() {
            ongoing.remove(recipient.getUniqueId());
        }
    }

    private static class CommandBlocker implements Listener {

        private final Set<UUID> targets;
        private final Predicate<String> filter;

        private CommandBlocker(Set<UUID> targets, Predicate<String> filter) {
            this.targets = targets;
            this.filter = filter;
        }

        @EventHandler
        public void onEvent(PlayerCommandPreprocessEvent event) {
            if (targets.contains(event.getPlayer().getUniqueId()) && filter.test(event.getMessage())) {
                event.setCancelled(true);
            }
        }
    }
}
