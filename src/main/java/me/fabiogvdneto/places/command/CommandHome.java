package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Home;
import me.fabiogvdneto.places.model.exception.HomeNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandHome extends CommandHandler<PlacesPlugin> {

    public CommandHome(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.home");
            requireArguments(args, 1);

            Player player = (Player) sender;
            Player target = (args.length == 1) ? player : parsePlayer(args, 1);

            plugin.getUsers().fetch(target.getUniqueId(), user -> {
                try {
                    Home home = user.getHome(args[0]);

                    plugin.teleport(player, home);
                } catch (HomeNotFoundException e) {
                    plugin.getMessages().homeNotFound(sender);
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalArgumentException e) {
            if (e.getIndex() == 1) {
                // The requested player is not online.
                plugin.getMessages().playerNotFound(sender, args[1]);
            } else {
                // Command was executed without arguments.
                plugin.getServer().dispatchCommand(sender, "homes");
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length > 2) return Collections.emptyList();

        // Returning null will list all the online players.
        if (args.length == 2) return null;

        Collection<Home> homes = plugin.getUsers().getIfCached(player.getUniqueId()).getHomes();
        return homes.stream().map(Home::getName).toList();
    }
}
