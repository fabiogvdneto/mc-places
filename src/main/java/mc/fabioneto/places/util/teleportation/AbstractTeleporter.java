package mc.fabioneto.places.util.teleportation;

import mc.fabioneto.places.util.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class AbstractTeleporter implements Teleporter {

    private final Plugin plugin;
    private final Map<UUID, BukkitRunnable> tasks = new HashMap<>();

    private int maxDelay;
    private boolean movAllowed;
    private boolean damAllowed;
    private CommandBlocker cmdBlocker;

    public AbstractTeleporter(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    protected abstract int computeDelay(Player player);

    protected abstract Message getCountdownMessage();

    protected abstract Message getTeleportedMessage();

    protected abstract Message getDamageNotAllowedMessage();

    protected abstract Message getMovementNotAllowedMessage();

    @Override
    public void teleport(Player player, Location dest) {
        Teleportation teleportation = new Teleportation(player, dest, computeDelay(player));
        BukkitRunnable task = tasks.put(player.getUniqueId(), teleportation);

        if (task != null) {
            task.cancel();
        }

        teleportation.start();
    }

    @Override
    public boolean cancel(UUID player) {
        BukkitRunnable task = tasks.get(player);

        if (task != null) {
            task.cancel();
            return true;
        }

        return false;
    }

    @Override
    public int getMaxDelay() {
        return maxDelay;
    }

    @Override
    public void setMaxDelay(int seconds) {
        this.maxDelay = seconds;
    }

    @Override
    public boolean isMovementAllowed() {
        return movAllowed;
    }

    @Override
    public void setMovementAllowed(boolean allowed) {
        this.movAllowed = allowed;
    }

    @Override
    public boolean isDamageAllowed() {
        return damAllowed;
    }

    @Override
    public void setDamageAllowed(boolean allowed) {
        this.damAllowed = allowed;
    }

    @Override
    public void modCommandBlocker(int mode, Collection<String> list) {
        if (mode == 0) {
            if (cmdBlocker != null) {
                cmdBlocker.unregister();
            }

            this.cmdBlocker = null;
            return;
        }

        Set<String> cmds = new HashSet<>(list);
        this.cmdBlocker = new CommandBlocker((mode < 0), cmds);
        cmdBlocker.register();
    }

    private class CommandBlocker implements Listener {

        // true mode = whitelist
        // false mode = blacklist
        private final boolean mode;
        private final Set<String> cmds;

        private CommandBlocker(boolean mode, Set<String> cmds) {
            this.mode = mode;
            this.cmds = cmds;
        }

        @EventHandler(priority = EventPriority.HIGH)
        public void onCommand(PlayerCommandPreprocessEvent e) {
            BukkitRunnable task = tasks.get(e.getPlayer().getUniqueId());

            if (task == null) return;

            String cmd = e.getMessage().substring(1);

            if (mode == cmds.contains(cmd)) return;

            e.setCancelled(true);
        }

        public void register() {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        public void unregister() {
            PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
        }
    }

    private class Teleportation extends BukkitRunnable {

        private final Player player;
        private final Location dest;

        private int counter;

        private double healthTracker;
        private int x;
        private int y;
        private int z;

        private Teleportation(Player player, Location dest, int delay) {
            this.player = Objects.requireNonNull(player);
            this.dest = Objects.requireNonNull(dest);
            this.counter = delay;
        }

        @Override
        public void run() {
            if (!damAllowed && isWeaker()) {
                getDamageNotAllowedMessage().send(player);
                cancel();
                return;
            }

            if (!movAllowed && hasMoved()) {
                getMovementNotAllowedMessage().send(player);
                cancel();
                return;
            }

            if (counter == 0) {
                player.teleport(dest);
                getTeleportedMessage().send(player);
                cancel();
                return;
            }

            getCountdownMessage().format(counter--).send(player);
        }

        private boolean isWeaker() {
            double health = player.getHealth();

            if (health < healthTracker) {
                return true;
            }

            healthTracker = health;
            return false;
        }

        private boolean hasMoved() {
            Location loc = player.getLocation();

            return (x != loc.getBlockX()) || (y != loc.getBlockY()) || (z != loc.getBlockZ());
        }

        private void start() {
            Location loc = player.getLocation();

            this.healthTracker = player.getHealth();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();

            runTaskTimer(plugin, 0, 20);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();

            tasks.remove(player.getUniqueId());
        }
    }
}
