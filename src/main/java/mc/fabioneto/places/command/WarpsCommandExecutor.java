package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

public class WarpsCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public WarpsCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        Collection<Place> warps = plugin.getPlaceManager().getContainer(null).getPlaces();

        if (warps.isEmpty()) {
            translate("warp.list.empty").send(sender);
            return;
        }

        String separator = translate("warp.list.separator").getContent();
        String list = warps.stream()
                .filter(w -> !w.isClosed() || sender.hasPermission("places.warp." + w.getName()))
                .map(Place::getName)
                .collect(Collectors.joining(separator));

        translate("warp.list.base").format(list).send(sender);
    }
}
