package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public HomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (args.length == 0) {
            p.performCommand("homes");
            return null;
        }

        Place home;

        if (args.length == 1) {
            home = plugin.getHomeContainer(p.getUniqueId()).getPlace(args[0]);
        } else {
            PlaceContainer container = plugin.getHomeContainer(args[0]);

            if (container == null) {
                return message("command.player-not-found");
            }

            home = container.getPlace(args[1]);

            if (home.isClosed() && !plugin.hasAdminPermission(p)) {
                return message("home.closed");
            }
        }

        if (home == null) {
            return message("home.not-found");
        }

        plugin.teleport(p, home);
        return null;
    }
}
