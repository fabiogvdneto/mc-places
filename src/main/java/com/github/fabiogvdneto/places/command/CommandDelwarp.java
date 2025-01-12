package com.github.fabiogvdneto.places.command;

import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.command.CommandHandler;
import com.github.fabiogvdneto.places.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.places.model.exception.WarpNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandDelwarp extends CommandHandler<PlacesPlugin> {

    public CommandDelwarp(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            plugin.getSettings().getCommandPermission(cmd).require(sender);
            requireArguments(args, 1);

            plugin.getWarps().delete(args[0]);
            plugin.getMessages().warpDeleted(sender);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }
}
