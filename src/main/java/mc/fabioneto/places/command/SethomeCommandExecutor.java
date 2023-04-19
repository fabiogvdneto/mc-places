package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SethomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public SethomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (args.length == 0) {
            return message("command.usage.sethome");
        }

        PlaceContainer container;

        if ((args.length > 1) && plugin.hasAdminPermission(p)) {
            container = plugin.getHomeContainer(args[0]);

            if (container == null) {
                return message("command.player-not-found");
            }
        } else {
            container = plugin.getHomeContainer(p.getUniqueId());
        }

        int limit = plugin.getHomeLimit(p);

        if (container.getPlaces().size() >= limit) {
            return message("home.limit-reached").format(limit);
        }

        Place place = container.createPlace(args[0], p.getLocation());

        return message((place == null) ? "home.already-exists" : "home.set");
    }
}
