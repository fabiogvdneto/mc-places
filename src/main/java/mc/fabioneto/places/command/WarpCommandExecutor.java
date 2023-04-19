package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public WarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (args.length == 0) {
            p.performCommand("warps");
            return null;
        }

        Place warp = plugin.getWarpContainer().getPlace(args[0]);

        if (warp == null) {
            return message("warp.not-found");
        }

        if (warp.isClosed() && !plugin.hasWarpPermission(p, warp.getName())) {
            return message("warp.closed");
        }

        plugin.teleport(p, warp);
        return null;
    }
}
