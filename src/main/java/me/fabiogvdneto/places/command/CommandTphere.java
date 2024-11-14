package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.exception.CommandArgumentException;
import me.fabiogvdneto.places.common.exception.PermissionRequiredException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTphere extends CommandHandler<PlacesPlugin> {

    public CommandTphere(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.tphere");
            requireArguments(args, 1);

            parsePlayer(args, 0).teleport((Player) sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, "tphere");
        }
    }
}
