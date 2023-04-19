package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelhomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public DelhomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (args.length == 0) {
            return message("command.usage.delhome");
        }

        PlaceContainer container;
        String name;

        if ((args.length > 1) && plugin.hasAdminPermission(p)) {
            container = plugin.getHomeContainer(args[0]);

            if (container == null) {
                return message("command.player-not-found");
            }

            name = args[1];
        } else {
            container = plugin.getHomeContainer(p.getUniqueId());
            name = args[0];
        }

        return message(container.removePlace(name) ? "home.set" : "home.not-found");
    }
}
