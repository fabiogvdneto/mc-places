package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

public class WarpsCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public WarpsCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        Collection<Place> warps = plugin.getWarpContainer().getPlaces();

        if (!plugin.hasAdminPermission(sender)) {
            warps.removeIf(w -> w.isClosed() && !plugin.hasWarpPermission(sender, w.getName()));
        }

        if (warps.isEmpty()) {
            return message("warp.list.empty");
        }

        String separator = message("warp.list.separator").getContent();
        String list = warps.stream()
                .map(Place::getName)
                .collect(Collectors.joining(separator));

        return message("warp.list.base").format(list);
    }
}
