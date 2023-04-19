package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelwarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public DelwarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (!p.hasPermission("warps.command.delwarp")) {
            return message("command.no-permission");
        }

        if (args.length == 0) {
            return message("command.usage.delwarp");
        }

        if (!plugin.getWarpContainer().removePlace(args[0])) {
            return message("warp.not-found");
        }

        return message("warp.deleted");
    }
}
