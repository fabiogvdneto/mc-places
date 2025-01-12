package com.github.fabiogvdneto.places.command;

import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.command.CommandHandler;
import com.github.fabiogvdneto.places.common.exception.CommandSenderException;
import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.places.model.Place;
import com.github.fabiogvdneto.places.model.exception.WarpNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn extends CommandHandler<PlacesPlugin> {

    public CommandSpawn(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            plugin.getSettings().getCommandPermission(cmd).require(sender);

            Place spawn = plugin.getWarps().get("spawn");
            plugin.teleport((Player) sender, spawn);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (WarpNotFoundException e) {
            plugin.getMessages().spawnNotFound(sender);
        }
    }
}
