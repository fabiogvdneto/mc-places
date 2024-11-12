package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.exception.WarpNotFoundException;
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
    public void onCommand(CommandSender sender, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.warp");
            requireArguments(args, 1);

            Place warp = plugin.getWarps().get(args[0]);

            if (!plugin.getSettings().hasWarpPermission(sender, warp.getName())) {
                plugin.getMessages().permissionRequired(sender);
                return;
            }

            plugin.teleport((Player) sender, warp);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalArgumentException e) {
            plugin.getServer().dispatchCommand(sender, "warps");
        } catch (WarpNotFoundException e) {
            plugin.getMessages().warpNotFound(sender);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        Collection<Place> warps = plugin.getWarps().getAll();
        Stream<String> stream = warps.stream().filter(warp -> !warp.isClosed()).map(Place::getName);

        return plugin.getSettings().hasAdminPermission(sender)
                ? stream.toList()
                : stream.filter(place -> plugin.getSettings().hasWarpPermission(sender, place)).toList();
    }
}
