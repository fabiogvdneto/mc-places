package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Place;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandWarps extends CommandHandler<PlacesPlugin> {

    public CommandWarps(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        try {
            requirePermission(sender, "places.command.warps");

            Collection<String> warps = plugin.getWarps().getAll().stream().map(Place::getName)
                    .filter(name -> plugin.getSettings().hasWarpPermission(sender, name)).toList();

            plugin.getMessages().warpList(sender, warps);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
