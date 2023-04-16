package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.place.Place;
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

        Collection<Place> homes;

        if (args.length == 0) {
            homes = getPlaces(p.getUniqueId());
        } else {
            UUID owner = plugin.getPlayerDatabase().fetchID(args[0]);

            if (owner == null) {
                lang.translate("command.player-not-found").send(p);
                return;
            }

            homes = getPlaces(owner);

            if (!p.hasPermission("places.admin")) {
                homes.removeIf(Place::isClosed);
            }
        }

        if (homes.isEmpty()) {
            lang.translate("home.list.empty").send(p);
            return;
        }

        String separator = lang.translate("home.list.separator").getContent();
        String list = homes.stream().map(Place::getName).collect(Collectors.joining(separator));

        lang.translate("home.list.base").format(list).send(p);
    }

    private Collection<Place> getPlaces(UUID owner) {
        return plugin.getPlaceManager().getContainer(owner).getPlaces();
    }
}
