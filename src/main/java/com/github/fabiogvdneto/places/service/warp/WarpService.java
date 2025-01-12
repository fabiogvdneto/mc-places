package com.github.fabiogvdneto.places.service.warp;

import com.github.fabiogvdneto.places.common.PluginService;
import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.Plugins;
import com.github.fabiogvdneto.places.model.Place;
import com.github.fabiogvdneto.places.model.WarpManager;
import com.github.fabiogvdneto.places.model.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.places.model.exception.WarpNotFoundException;
import com.github.fabiogvdneto.places.repository.WarpRepository;
import com.github.fabiogvdneto.places.repository.data.WarpData;
import com.github.fabiogvdneto.places.repository.java.JavaWarpSingleRepository;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WarpService implements WarpManager, PluginService {

    private final PlacesPlugin plugin;
    private final Map<String, Place> cache = new HashMap<>();

    private BukkitTask autosaveTask;
    private WarpRepository repository;

    public WarpService(PlacesPlugin plugin) {
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
            repository.create();
            repository.fetch().forEach(data -> cache.put(data.name().toLowerCase(), new StandardWarp(data)));
            plugin.getLogger().info("Loaded " + cache.size() + " warps.");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load warp data.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void autosave() {
        int ticks = plugin.getSettings().getWarpAutosaveInterval() * 60 * 20;

        if (ticks > 0) {
            // 36.000 ticks = 30 minutes
            this.autosaveTask = Plugins.sync(plugin, () -> {
                Collection<WarpData> data = memento();
                Plugins.async(plugin, () -> save(data));
            }, ticks, ticks);
        }
    }

    @Override
    public void disable() {
        if (repository == null)  return;

        autosaveTask.cancel();
        save(memento());

        this.autosaveTask = null;
        this.repository = null;
    }

    private void save(Collection<WarpData> data) {
        try {
            repository.store(data);
            plugin.getLogger().info("Saved " + data.size() + " warps.");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save warps.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private Collection<WarpData> memento() {
        return cache.values().stream().map(warp -> ((StandardWarp) warp).data()).toList();
    }

    public WarpRepository getRepository() {
        return repository;
    }
}
