package com.github.fabiogvdneto.places.command;

import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.command.CommandHandler;
import com.github.fabiogvdneto.places.common.exception.CommandSenderException;
import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBack extends CommandHandler<PlacesPlugin> {

    public CommandBack(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            plugin.getSettings().getCommandPermission(cmd).require(sender);

            plugin.teleportBack((Player) sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalStateException e) {
            plugin.getMessages().cannotTeleportBack(sender);
        }
    }
}
