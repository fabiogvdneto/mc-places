package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
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
        if ((args.length != 1) || !(sender instanceof Player p)) {
            return Collections.emptyList();
        }

        return plugin.getPlaceManager().getContainer(p.getUniqueId()).getPlaces().stream()
                .map(Place::getName)
                .filter(name -> name.startsWith(args[0]))
                .toList();
    }
}
