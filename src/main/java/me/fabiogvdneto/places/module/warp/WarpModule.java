package me.fabiogvdneto.places.module.warp;

import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.Plugins;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.WarpManager;
import me.fabiogvdneto.places.model.exception.WarpAlreadyExistsException;
import me.fabiogvdneto.places.model.exception.WarpNotFoundException;
import me.fabiogvdneto.places.repository.WarpRepository;
import me.fabiogvdneto.places.repository.data.WarpData;
import me.fabiogvdneto.places.repository.java.JavaWarpSingleRepository;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

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
        disable();
        createRepository();
        autosave();
    }

    private void createRepository() {
        this.repository = new JavaWarpSingleRepository(plugin.getDataPath().resolve("data").resolve("warps.ser"));

        try {
            repository.mount();
            repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new StandardWarp(data)));
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while trying to load warp data.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getWarpAutosaveInterval() * 60 * 20;

        // 36.000 ticks = 30 minutes
        this.autosaveTask = Plugins.sync(plugin, () -> {
            Collection<WarpData> data = memento();

            Plugins.async(plugin, () -> {
                plugin.getLogger().info("Saving warps...");
                try {
                    repository.store(data);
                } catch (Exception e) {
                    plugin.getLogger().warning("An error occurred while trying to save data.");
                    plugin.getLogger().warning(e.getMessage());
                }
                plugin.getLogger().info("Finished saving warps.");
            });
        }, ticks, ticks);
    }

    @Override
    public void disable() {
        if (autosaveTask == null) return;

        autosaveTask.cancel();

        try {
            repository.store(memento());
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while trying to save warps.");
            plugin.getLogger().warning(e.getMessage());
        }

        this.autosaveTask = null;
        this.repository = null;
    }

    private Collection<WarpData> memento() {
        return cache.values().stream().map(warp -> ((StandardWarp) warp).data()).toList();
    }

    public WarpRepository getRepository() {
        return repository;
    }
}
