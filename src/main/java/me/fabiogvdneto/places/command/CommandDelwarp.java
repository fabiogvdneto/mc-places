package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.exception.WarpNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandDelwarp extends CommandHandler<PlacesPlugin> {

    public CommandDelwarp(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePermission(sender, "places.command.delwarp");
            requireArguments(args, 1);

            plugin.getWarps().delete(args[0]);
            plugin.getMessages().warpDeleted(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }
}
