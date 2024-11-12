package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTphere extends CommandHandler<PlacesPlugin> {

    public CommandTphere(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.tphere");
            requireArguments(args, 1);

            parsePlayer(args, 0).teleport((Player) sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalArgumentException e) {
            plugin.getMessages().commandUsage(sender, "tphere");
        }
    }
}
