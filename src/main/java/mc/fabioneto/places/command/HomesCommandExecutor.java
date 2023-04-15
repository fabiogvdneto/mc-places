package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomesCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public HomesCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            lang.translate("command.players-only").send(sender);
            return;
        }

        UUID owner = (args.length > 0)
                ? plugin.getPlayerDatabase().fetchID(args[0])
                : p.getUniqueId();

        if (owner == null) {
            lang.translate("home.player-not-found").send(p);
            return;
        }

        Collection<Place> homes = plugin.getPlaceManager().getContainer(owner).getPlaces();

        if (homes.isEmpty()) {
            lang.translate("home.list.empty").send(p);
            return;
        }

        String separator = lang.translate("home.list.separator").getContent();
        String list;

        if ((args.length > 0) && !p.hasPermission("places.admin")) {
            list = homes.stream()
                    .filter(h -> !h.isClosed())
                    .map(Place::getName)
                    .collect(Collectors.joining(separator));
        } else {
            list = homes.stream().map(Place::getName).collect(Collectors.joining(separator));
        }

        lang.translate("home.list.base").format(list).send(p);
    }
}
