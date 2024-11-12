package me.fabiogvdneto.places.module.warp;

import com.google.common.base.Preconditions;
import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.Plugins;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.WarpManager;
import me.fabiogvdneto.places.model.exception.WarpAlreadyExistsException;
import me.fabiogvdneto.places.model.exception.WarpNotFoundException;
import me.fabiogvdneto.places.repository.WarpRepository;
import me.fabiogvdneto.places.repository.data.WarpData;
import me.fabiogvdneto.places.repository.java.JavaWarpRepository;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WarpModule implements WarpManager, PlacesModule {

    private final PlacesPlugin plugin;
    private final Map<String, Place> cache = new HashMap<>();

    private BukkitTask autosaveTask;
    private WarpRepository repository;

    public WarpModule(PlacesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<Place> getAll() {
        return cache.values();
    }

    @Override
    public Place get(String name) throws WarpNotFoundException {
        Place place = cache.get(name.toLowerCase());

        if (place == null)
            throw new WarpNotFoundException();

        return place;
    }

    @Override
    public Place create(String name, Location location) throws WarpAlreadyExistsException {
        Place warp = new StandardWarp(name, location);

        if (cache.putIfAbsent(name.toLowerCase(), warp) != null)
            throw new WarpAlreadyExistsException();

        return warp;
    }

    @Override
    public void delete(String name) throws WarpNotFoundException {
        if (cache.remove(name.toLowerCase()) == null)
            throw new WarpNotFoundException();
    }

    @Override
    public void enable() {
        Preconditions.checkState(repository == null, "warp module is already enabled");

        createRepository();
        autosave();
    }

    private void createRepository() {
        File file = new File(plugin.getDataFolder(), "warps.bin");

        this.repository = new JavaWarpRepository(file);

        try {
            repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new StandardWarp(data)));
        } catch (IOException e) {
            plugin.getLogger().warning("[Warps] An error occurred while trying to load data.");
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getWarpAutosaveInterval() * 60 * 20;

        // 36.000 ticks = 30 minutes
        this.autosaveTask = Plugins.sync(plugin, () -> {
            Collection<WarpData> data = data();

            Plugins.async(plugin, () -> {
                plugin.getLogger().info("[Warps] Saving data...");
                try {
                    repository.store(data);
                } catch (IOException e) {
                    plugin.getLogger().warning("[Warps] An error occurred while trying to save data.");
                }
                plugin.getLogger().info("[Warps] Save completed.");
            });
        }, ticks, ticks);
    }

    @Override
    public void disable() {
        Preconditions.checkState(repository != null, "warp module is already disabled");

        autosaveTask.cancel();

        try {
            repository.store(data());
        } catch (IOException e) {
            plugin.getLogger().warning("[Warps] An error occurred while trying to save data.");
        }

        this.autosaveTask = null;
        this.repository = null;
    }

    private Collection<WarpData> data() {
        return cache.values().stream().map(warp -> ((StandardWarp) warp).data()).toList();
    }

    public WarpRepository getRepository() {
        return repository;
    }
}
