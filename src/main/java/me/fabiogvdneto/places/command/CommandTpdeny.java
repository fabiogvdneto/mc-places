package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.TeleportationRequest;
import me.fabiogvdneto.places.model.exception.TeleportationRequestClosedException;
import me.fabiogvdneto.places.model.exception.TeleportationRequestNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandTpdeny extends CommandHandler<PlacesPlugin> {

    public CommandTpdeny(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.tpdeny");

            Player player = (Player) sender;
            Player target = (args.length == 0) ? player : parsePlayer(args, 0);

            plugin.getUsers().fetch(player.getUniqueId(), user -> {
                try {
                    user.getTeleportationRequest(target.getUniqueId()).deny();
                    plugin.getMessages().teleportationRequestDenied(player, target.getName());
                } catch (TeleportationRequestNotFoundException e) {
                    plugin.getMessages().teleportationRequestNotFound(player);
                } catch (TeleportationRequestClosedException e) {
                    plugin.getMessages().teleportationRequestExpired(player, target.getName());
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalArgumentException e) {
            plugin.getMessages().playerNotFound(sender, args[0]);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        Collection<TeleportationRequest> requests = plugin.getUsers().getIfCached(player.getUniqueId()).getTeleportationRequests();

        return requests.stream()
                .map(TeleportationRequest::getSender)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(Player::getName)
                .toList();
    }
}