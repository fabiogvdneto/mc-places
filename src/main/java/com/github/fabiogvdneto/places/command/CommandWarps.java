package com.github.fabiogvdneto.places.command;

import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.command.CommandHandler;
import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.places.model.Place;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class CommandWarps extends CommandHandler<PlacesPlugin> {

    public CommandWarps(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            plugin.getSettings().getCommandPermission(cmd).require(sender);

            Collection<String> warps = plugin.getWarps().getAll().stream().map(Place::getName)
                    .filter(name -> plugin.getSettings().getWarpPermission(name).test(sender)).toList();

            plugin.getMessages().warpList(sender, warps);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        }
    }
}
