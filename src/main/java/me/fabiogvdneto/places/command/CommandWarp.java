package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.exception.CommandArgumentException;
import me.fabiogvdneto.places.common.exception.CommandSenderException;
import me.fabiogvdneto.places.common.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.exception.WarpNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class CommandWarp extends CommandHandler<PlacesPlugin> {

    public CommandWarp(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            plugin.getSettings().getCommandPermission(cmd).require(sender);
            requireArguments(args, 1);

            Place warp = plugin.getWarps().get(args[0]);

            if (!plugin.getSettings().getWarpPermission(warp.getName()).test(sender)) {
                plugin.getMessages().permissionRequired(sender);
                return;
            }

            plugin.teleport((Player) sender, warp);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getServer().dispatchCommand(sender, "warps");
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        Collection<Place> warps = plugin.getWarps().getAll();
        Stream<String> stream = warps.stream().filter(warp -> !warp.isClosed()).map(Place::getName);

        return plugin.getSettings().getAdminPermission().test(sender)
                ? stream.toList()
                : stream.filter(place -> plugin.getSettings().getWarpPermission(place).test(sender)).toList();
    }
}
