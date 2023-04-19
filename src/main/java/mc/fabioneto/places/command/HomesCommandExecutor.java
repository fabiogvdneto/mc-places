package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class HomesCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public HomesCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        Collection<Place> homes;

        if (args.length == 0) {
            homes = plugin.getHomeContainer(p.getUniqueId()).getPlaces();
        } else {
            PlaceContainer container = plugin.getHomeContainer(args[0]);

            if (container == null) {
                return message("command.player-not-found");
            }

            homes = container.getPlaces();

            if (!plugin.hasAdminPermission(p)) {
                homes.removeIf(Place::isClosed);
            }
        }

        if (homes.isEmpty()) {
            return message("home.list.empty");
        }

        String separator = lang.translate("home.list.separator").getContent();
        String list = homes.stream().map(Place::getName).collect(Collectors.joining(separator));

        return message("home.list.base").format(list);
    }
}
