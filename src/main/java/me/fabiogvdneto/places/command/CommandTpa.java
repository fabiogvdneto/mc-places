package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import me.fabiogvdneto.places.model.TeleportationRequest;
import me.fabiogvdneto.places.model.exception.TeleportationRequestAlreadyExistsException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class CommandTpa extends CommandHandler<PlacesPlugin> {

    public CommandTpa(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.tpa");
            requireArguments(args, 1);

            Player player = (Player) sender;
            Player target = parsePlayer(args, 0);

            if (player == target) {
                plugin.getMessages().teleportationRequestYourself(sender);
                return;
            }

            plugin.getUsers().fetch(target.getUniqueId(), user -> {
                try {
                    Duration duration = Duration.ofSeconds(plugin.getSettings().getTeleportationRequestDuration());

                    user.createTeleportationRequest(player.getUniqueId(), duration);
                    plugin.getMessages().teleportationRequestSent(player, target.getName());
                    plugin.getMessages().teleportationRequestReceived(target, player.getName());
                } catch (TeleportationRequestAlreadyExistsException e) {
                    TeleportationRequest request = e.getValue();

                    if (request.getState() == TeleportationRequest.State.OPEN) {
                        plugin.getMessages().teleportationRequestPending(player);
                    } else {
                        plugin.getMessages().teleportationRequestCooldown(player);
                    }
                }
            });
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (IllegalSenderException e) {
            plugin.getMessages().playersOnly(sender);
        } catch (IllegalArgumentException e) {
            if (e.getIndex() == 0) {
                plugin.getMessages().playerNotFound(sender, args[0]);
            } else {
                plugin.getMessages().commandUsage(sender, label);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        // Returning null will list all the online players.
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
