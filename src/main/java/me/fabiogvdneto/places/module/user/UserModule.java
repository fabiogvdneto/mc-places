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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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

    private User getOrNull(CompletableFuture<User> future) {
        try {
            return future.getNow(null);
        } catch (CompletionException | CancellationException e) {
            return null;
        }
    }

    @Override
    public User getIfCached(UUID userId) {
        CompletableFuture<User> future = cache.get(userId);

        return (future == null) ? null : getOrNull(future);
    }

    @Override
    public void fetch(UUID userId, Consumer<User> callback) {
        load(userId).whenComplete((user, x) -> {
            if (user != null) {
                callback.accept(user);
            }
        });
    }

    private CompletableFuture<User> load(UUID userId) {
        return cache.computeIfAbsent(userId, key -> {
            CompletableFuture<User> future = new CompletableFuture<>();

            Plugins.async(plugin, () -> {
                try {
                    UserData data = repository.select(userId).fetch();

                    future.complete(new StandardUser(data));
                } catch (IOException x) {
                    future.completeExceptionally(x);
                }
            });

            return future;
        });
    }

    @Override
    public void enable() {
        this.cache.clear();
        this.repository = new JavaUserRepository(new File(plugin.getDataFolder(), "users"));

        registerEvents();
        loadOnlinePlayers();
        autosave();
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

    private void loadOnlinePlayers() {
        Player[] snapshot = Bukkit.getOnlinePlayers().toArray(Player[]::new);

        for (Player player : snapshot) {
            load(player.getUniqueId());
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getUserAutosaveInterval() * 60 * 20;
        int purgeDays = plugin.getSettings().getUserPurgeDays();

        this.autosaveTask = Plugins.sync(plugin, () -> {
            plugin.getLogger().info("[Users] Saving user data...");

            // Copy cached data so that it can be stored asynchronously without race conditions.
            Collection<UserData> snapshot = data();

            // Execute repository operations asynchronously.
            Plugins.async(plugin, () -> {
                // Save all cached data.
                snapshot.forEach(data -> {
                    try {
                        repository.select(data.uid()).store(data);
                    } catch (IOException e) {
                        plugin.getLogger().warning("[Users] An error occurred while trying to save user data.");
                    }
                });

                // Remove (purge) old data from the repository.
                try {
                    repository.purge(purgeDays);
                } catch (IOException e) {
                    plugin.getLogger().warning("[Users] An error occurred while trying to purge user data.");
                }
            });

            // Remove (purge) offline players from the cache.
            refreshCache();

            plugin.getLogger().info("[Users] User data saved successfully.");
        }, ticks, ticks);
    }

    private void refreshCache() {
        Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getUniqueId).collect(Collectors.toUnmodifiableSet());

        cache.keySet().retainAll(onlinePlayers);
    }

    @Override
    public void disable() {
        PlayerJoinEvent.getHandlerList().unregister(playerListener);

        autosaveTask.cancel();

        data().forEach(data -> {
            try {
                repository.select(data.uid()).store(data);
            } catch (IOException e) {
                plugin.getLogger().warning("[Users] An error occurred while trying to save user data.");
            }
        });

        this.autosaveTask = null;
        this.repository = null;
    }

    private List<UserData> data() {
        return cache.values().stream()
                .map(this::getOrNull)
                .filter(Objects::nonNull)
                .map(user -> ((StandardUser) user).data())
                .toList();
    }
}
