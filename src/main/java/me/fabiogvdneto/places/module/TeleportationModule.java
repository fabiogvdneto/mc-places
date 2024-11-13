package me.fabiogvdneto.places.module;

import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.teleporter.PluginTeleporter;
import me.fabiogvdneto.places.common.teleporter.Teleportation;
import me.fabiogvdneto.places.common.teleporter.Teleporter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TeleportationModule implements PlacesModule, Teleporter {

    private final PlacesPlugin plugin;
    private PluginTeleporter teleporter;

    public TeleportationModule(PlacesPlugin plugin) {
        this.teleporter = new PluginTeleporter(plugin);
        this.plugin = plugin;
    }

    @Override
    public Collection<Teleportation> ongoing() {
        return teleporter.ongoing();
    }

    @Override
    public Teleportation ongoing(Player recipient) {
        return teleporter.ongoing(recipient);
    }

    @Override
    public Teleportation create(Player recipient, Location dest) {
        return teleporter.create(recipient, dest);
    }

    @Override
    public Teleportation create(Player recipient, Supplier<Location> dest) {
        return teleporter.create(recipient, dest);
    }

    @Override
    public Location back(Player recipient) {
        return teleporter.back(recipient);
    }

    @Override
    public void enable() {
        teleporter.setCommandBlocker(buildCommandFilter());
    }

    private Predicate<String> buildCommandFilter() {
        final String allowed = plugin.getSettings().getTeleportationCommandsAllowed().toLowerCase();
        final Set<String> list = plugin.getSettings().getTeleportationCommandList();

        return switch (allowed) {
            // Blacklist: block everything in the list.
            case "blacklist" -> (list::contains);
            // Whitelist: block everything not in the list.
            case "whitelist" -> (cmd -> !list.contains(cmd));
            // False: block everything.
            case "false"     -> (cmd -> false);
            // Unknown value: allow all commands (no need to register command blocker).
            default -> null;
        };
    }

    @Override
    public void disable() {
        teleporter.setCommandBlocker(null);
        teleporter = null;
    }
}
