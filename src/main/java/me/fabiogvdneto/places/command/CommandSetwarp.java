package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.exception.CommandArgumentException;
import me.fabiogvdneto.places.common.exception.CommandSenderException;
import me.fabiogvdneto.places.common.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.exception.WarpAlreadyExistsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetwarp extends CommandHandler<PlacesPlugin> {

    public CommandSetwarp(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.setwarp");
            requireArguments(args, 1);

            Player player = (Player) sender;

            plugin.getWarps().create(args[0], player.getLocation());
            plugin.getMessages().warpSet(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        } catch (WarpAlreadyExistsException e) {
            plugin.getMessages().warpAlreadyExists(sender);
        }
    }
}
