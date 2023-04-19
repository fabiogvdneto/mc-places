package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.util.command.AbstractTabCompleter;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WarpTabCompleter extends AbstractTabCompleter<PlacesPlugin> {

    public WarpTabCompleter(PlacesPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return plugin.getWarpContainer().getPlaces().stream()
                .filter(w -> !w.isClosed() || plugin.hasWarpPermission(sender, w.getName()))
                .map(Place::getName)
                .filter(name -> name.startsWith(args[0]))
                .toList();
    }
}
