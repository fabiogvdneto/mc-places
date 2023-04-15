package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractTabCompleter;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WarpTabCompleter extends AbstractTabCompleter<PlacesPlugin> {

    public WarpTabCompleter(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return plugin.getPlaceManager().getContainer(null).getPlaces().stream()
                .filter(w -> !w.isClosed() || sender.hasPermission("places.warp." + w.getName()))
                .map(Place::getName)
                .filter(name -> name.startsWith(args[0]))
                .toList();
    }
}
