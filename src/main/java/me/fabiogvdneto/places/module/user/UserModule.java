package me.fabiogvdneto.places.module.user;

import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.Plugins;
import me.fabiogvdneto.places.model.User;
import me.fabiogvdneto.places.model.UserManager;
import me.fabiogvdneto.places.repository.UserRepository;
import me.fabiogvdneto.places.repository.data.UserData;
import me.fabiogvdneto.places.repository.java.JavaUserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UserModule implements UserManager, PlacesModule {

    private final PlacesPlugin plugin;
    private final Map<UUID, CompletableFuture<User>> cache = new HashMap<>();

    private UserRepository repository;
    private BukkitTask autosaveTask;
    private Listener playerListener;

    public UserModule(PlacesPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Collection<User> getAll() {
        return cache.values().stream().map(future -> future.getNow(null)).filter(Objects::nonNull).toList();
    }

    @Override
    public User getIfCached(UUID userId) {
        CompletableFuture<User> future = cache.get(userId);
        return (future == null) ? null : future.getNow(null);
    }

    @Override
    public void fetch(UUID userId, Consumer<User> callback) {
        load(userId).thenAccept(callback);
    }

    private CompletableFuture<User> load(UUID userId) {
        return cache.computeIfAbsent(userId, key -> {
            CompletableFuture<User> future = new CompletableFuture<>();

            Plugins.async(plugin, () -> {
                try {
                    UserData data = repository.fetchOne(userId);
                    StandardUser user = (data == null)
                            ? new StandardUser(userId)
                            : new StandardUser(data);
                    future.complete(user);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future.exceptionally(e -> {
                plugin.getLogger().warning("An error occurred while trying to load user data.");
                return null;
            });
        });
    }

    @Override
    public void enable() {
        disable();
        createRepository();
        registerEvents();
        loadOnlinePlayers();
        runAutosave();
    }

    private void registerEvents() {
        this.playerListener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                load(event.getPlayer().getUniqueId());
            }
        };

        plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);
    }

    private void createRepository() {
        this.repository = new JavaUserRepository(plugin.getDataPath().resolve("data").resolve("users"));

        try {
            repository.mount();
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while trying to create the user repository.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void loadOnlinePlayers() {
        Player[] snapshot = Bukkit.getOnlinePlayers().toArray(Player[]::new);

        plugin.getLogger().info("Loading user data...");
        for (Player player : snapshot) {
            load(player.getUniqueId());
        }
    }

    private void runAutosave() {
        int ticks = plugin.getSettings().getUserAutosaveInterval() * 60 * 20;
        int purgeDays = plugin.getSettings().getUserPurgeDays();

        this.autosaveTask = Plugins.sync(plugin, () -> {
            plugin.getLogger().info("Saving user data...");

            // Copy cached data so that it can be stored asynchronously without race conditions.
            Collection<UserData> snapshot = memento();

            // Execute repository operations asynchronously.
            Plugins.async(plugin, () -> {
                // Save all cached data.
                snapshot.forEach(data -> {
                    try {
                        repository.storeOne(data);
                    } catch (Exception e) {
                        plugin.getLogger().warning("An error occurred while trying to save user data.");
                        plugin.getLogger().warning(e.getMessage());
                    }
                });

                // Remove (purge) old data from the repository.
                try {
                    repository.purge(purgeDays);
                } catch (Exception e) {
                    plugin.getLogger().warning("An error occurred while trying to purge user data.");
                    plugin.getLogger().warning(e.getMessage());
                }
            });

            // Remove (purge) offline players from the cache.
            refreshCache();
        }, ticks, ticks);
    }

    private void refreshCache() {
        Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());

        cache.keySet().retainAll(onlinePlayers);
    }

    @Override
    public void disable() {
        if (autosaveTask == null) return;

        PlayerJoinEvent.getHandlerList().unregister(playerListener);
        autosaveTask.cancel();

        plugin.getLogger().info("Saving user data...");
        memento().forEach(data -> {
            try {
                repository.storeOne(data);
            } catch (Exception e) {
                plugin.getLogger().warning("An error occurred while trying to save user data.");
                plugin.getLogger().warning(e.getMessage());
            }
        });

        this.playerListener = null;
        this.autosaveTask = null;
        this.repository = null;
        this.cache.clear();
    }

    private List<UserData> memento() {
        return cache.values().stream()
                .map(future -> future.getNow(null))
                .filter(Objects::nonNull)
                .map(user -> ((StandardUser) user).memento())
                .toList();
    }
}
