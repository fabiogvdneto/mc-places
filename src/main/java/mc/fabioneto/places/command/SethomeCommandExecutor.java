package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.util.place.PlaceContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SethomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public SethomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            lang.translate("command.players-only").send(sender);
            return;
        }

        if (args.length == 0) {
            lang.translate("command.usage.sethome").send(sender);
            return;
        }

        UUID owner;

        if ((args.length > 1) && p.hasPermission("places.admin")) {
            owner = plugin.getPlayerDatabase().fetchID(args[0]);

            if (owner == null) {
                lang.translate("command.player-not-found").send(p);
                return;
            }
        } else {
            owner = p.getUniqueId();
        }

        int limit = computeLimit(p);

        PlaceContainer container = plugin.getPlaceManager().getContainer(owner);

        if (container.getPlaces().size() >= limit) {
            lang.translate("home.limit-reached").format(limit).send(p);
            return;
        }

        Place place = container.createPlace(args[0], p.getLocation());

        if (place == null) {
            lang.translate("home.already-exists").send(p);
            return;
        }

        lang.translate("home.set").send(p);
    }

    private int computeLimit(Player player) {
        for (int limit = plugin.getConfig().getInt("max-home-limit"); limit > 0; limit--) {
            if (player.hasPermission("places.home-limit." + limit)) {
                return limit;
            }
        }

        return 0;
    }
}
