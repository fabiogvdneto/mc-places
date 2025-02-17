package com.github.fabiogvdneto.places.command;

import com.github.fabiogvdneto.places.PlacesPlugin;
import com.github.fabiogvdneto.places.common.command.CommandHandler;
import com.github.fabiogvdneto.places.common.exception.CommandArgumentException;
import com.github.fabiogvdneto.places.common.exception.CommandSenderException;
import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import com.github.fabiogvdneto.places.model.TeleportationRequest;
import com.github.fabiogvdneto.places.model.exception.TeleportationRequestClosedException;
import com.github.fabiogvdneto.places.model.exception.TeleportationRequestNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandTpaccept extends CommandHandler<PlacesPlugin> {

    public CommandTpaccept(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            plugin.getSettings().getCommandPermission(cmd).require(sender);

            Player player = (Player) sender;
            Player target = (args.length == 0) ? player : parsePlayer(args, 0);

            plugin.getUsers().fetch(player.getUniqueId(), user -> {
                try {
                    user.getTeleportationRequest(target.getUniqueId()).accept();

                    plugin.getMessages().teleportationRequestAccepted(player, target.getName());
                    plugin.teleport(target, player);
                } catch (TeleportationRequestNotFoundException e) {
                    plugin.getMessages().teleportationRequestNotFound(player);
                } catch (TeleportationRequestClosedException e) {
                    plugin.getMessages().teleportationRequestExpired(player, target.getName());
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (CommandArgumentException e) {
            plugin.getMessages().playerNotFound(sender, args[0]);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
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
