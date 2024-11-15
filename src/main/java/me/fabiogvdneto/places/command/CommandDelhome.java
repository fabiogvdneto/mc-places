package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.exception.CommandArgumentException;
import me.fabiogvdneto.places.common.exception.CommandSenderException;
import me.fabiogvdneto.places.common.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Home;
import me.fabiogvdneto.places.model.exception.HomeNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandDelhome extends CommandHandler<PlacesPlugin> {

    public CommandDelhome(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            plugin.getSettings().getCommandPermission(cmd).require(sender);
            requireArguments(args, 1);

            Player player = (Player) sender;

            plugin.getUsers().fetch(player.getUniqueId(), user -> {
                try {
                    user.deleteHome(args[0]);
                    plugin.getMessages().homeDeleted(player);
                } catch (HomeNotFoundException e) {
                    plugin.getMessages().homeNotFound(player);
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().commandUsage(sender, label);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length > 1) return Collections.emptyList();

        Collection<Home> homes = plugin.getUsers().getIfCached(player.getUniqueId()).getHomes();
        return homes.stream().map(Home::getName).toList();
    }
}