package me.fabiogvdneto.places.module;

import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permissible;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SettingsModule implements PlacesModule {

    private final PlacesPlugin plugin;

    public SettingsModule(PlacesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        // Save raw configuration (preserve all comments)
        // Not needed in future versions of spigot/paper (1.18+).
        plugin.saveDefaultConfig();
        // Save configuration defaults.
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    @Override
    public void disable() {
        // Nothing to do.
    }

    /* ---- File Configuration ---- */

    private FileConfiguration config() {
        return plugin.getConfig();
    }

    public int getTeleporterDelay(Permissible perm) {
        int max = config().getInt("teleporter.max-delay-seconds");

        return IntStream.range(0, max)
                .filter(i -> perm.hasPermission("places.teleporter.delay." + i))
                .findFirst().orElse(max);
    }

    public int getTeleporterDelayForTpa(Permissible perm) {
        int delay = getTeleporterDelay(perm);
        int min = config().getInt("tpask.min-delay-seconds");

        return Math.max(min, delay);
    }

    public boolean isMovementAllowedWhileTeleporting() {
        return config().getBoolean("teleporter.movement-allowed");
    }

    public boolean isDamageAllowedWhileTeleporting() {
        return config().getBoolean("teleporter.damage-allowed");
    }

    public String getTeleportationCommandsAllowed() {
        return config().getString("teleporter.commands-allowed");
    }

    public Set<String> getTeleportationCommandList() {
        return config().getStringList("teleporter.command-list").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int getTeleportationRequestDuration() {
        return config().getInt("tpask.duration-seconds");
    }

    public int getWarpAutosaveInterval() {
        return config().getInt("warps.autosave-minutes");
    }

    public int getUserAutosaveInterval() {
        return config().getInt("users.autosave-minutes");
    }

    public int getUserPurgeDays() {
        return config().getInt("users.purge-days");
    }

    public int getHomeLimit(Permissible perm) {
        int i = config().getInt("homes.max-limit");

        if (perm.hasPermission("places.homes.limit.max")) return i;

        while (i > 0 && !perm.hasPermission("places.homes.limit." + i)) i--;

        return i;
    }

    /* ---- Permissions ---- */

    public boolean hasAdminPermission(Permissible perm) {
        return perm.hasPermission("places.admin");
    }

    public boolean hasCommandPermission(Permissible perm, String cmd) {
        return perm.hasPermission("places.command." + cmd);
    }

    public boolean hasWarpPermission(Permissible perm, String warpName) {
        return perm.hasPermission("places.warps.warp." + warpName);
    }
}
