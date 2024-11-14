package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Home;
import me.fabiogvdneto.places.model.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class CommandHomes extends CommandHandler<PlacesPlugin> {

    public CommandHomes(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.homes");

            Player player = (Player) sender;

            Consumer<User> callback = user -> {
                if (user == null) {
                    plugin.getMessages().homeListEmpty(sender);
                } else {
                    List<String> homes = user.getHomes().stream().map(Home::getName).toList();

                    plugin.getMessages().homeList(sender, homes);
                }
            };

            if (args.length == 0) {
                plugin.getUsers().fetch(player.getUniqueId(), callback);
            } else {
                Player target = parsePlayer(args, 0);
                plugin.getUsers().fetch(target.getUniqueId(), callback);
            }
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalArgumentException e) {
            plugin.getMessages().playerNotFound(sender, args[0]);
        }
    }
}
