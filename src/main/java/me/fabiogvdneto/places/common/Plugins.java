package me.fabiogvdneto.places.common;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Plugins {

    public static BukkitTask async(Plugin plugin, Runnable task) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask async(Plugin plugin, Runnable task, long delay) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public static BukkitTask async(Plugin plugin, Runnable task, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task, long delay) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    public static BukkitTask sync(Plugin plugin, Runnable task, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    public static YamlConfiguration loadResource(Plugin plugin, String path) {
        InputStream resource = plugin.getResource(path);

        return (resource == null) ? null : YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
    }

    public static YamlConfiguration loadConfiguration(Plugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);

        YamlConfiguration defaults = loadResource(plugin, path);
        YamlConfiguration config = file.isFile()
                ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();

        if (defaults != null) {
            try {
                config.setDefaults(defaults);
                config.options().copyDefaults(true);
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save configuration file.");
            }
        }

        return config;
    }


}
