package me.fabiogvdneto.places.command;

import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.command.CommandHandler;
import me.fabiogvdneto.places.common.exception.CommandSenderException;
import me.fabiogvdneto.places.common.exception.PermissionRequiredException;
import me.fabiogvdneto.places.common.teleporter.Teleportation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpcancel extends CommandHandler<PlacesPlugin> {

    public CommandTpcancel(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            requirePlayer(sender);
            requirePermission(sender, "places.command.tpcancel");

            Player player = (Player) sender;
            Teleportation ongoing = plugin.getTeleporter().ongoing(player);

            if (ongoing == null) {
                plugin.getMessages().teleportationNotFound(player);
                return;
            }

            ongoing.cancel();
            plugin.getMessages().teleportationCancelled(player);
        } catch (PermissionRequiredException e) {
            plugin.getMessages().permissionRequired(sender);
        } catch (CommandSenderException e) {
            plugin.getMessages().playersOnly(sender);
        }
    }
}
