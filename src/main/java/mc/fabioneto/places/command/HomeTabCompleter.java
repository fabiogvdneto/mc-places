package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.util.command.AbstractTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HomeTabCompleter extends AbstractTabCompleter<PlacesPlugin> {

    public HomeTabCompleter(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if ((args.length > 2) || !(sender instanceof Player p)) {
            return Collections.emptyList();
        }

        PlaceContainer container;
        String prefix;

        if (args.length == 1) {
            container = plugin.getHomeContainer(p.getUniqueId());
            prefix = args[0];
        } else {
            container = plugin.getHomeContainer(args[0]);
            prefix = args[1];
        }

        return (container == null) ? Collections.emptyList() : container.getPlaces().stream()
                .map(Place::getName)
                .filter(name -> name.startsWith(prefix))
                .toList();
    }
}
