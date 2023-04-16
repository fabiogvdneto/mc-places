package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.place.Place;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public HomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            lang.translate("command.players-only").send(sender);
            return;
        }

        if (args.length == 0) {
            p.performCommand("homes");
            return;
        }

        Place home;

        if (args.length == 1) {
            home = getPlace(p.getUniqueId(), args[0]);
        } else {
            UUID owner = plugin.getPlayerDatabase().fetchID(args[1]);

            if (owner == null) {
                lang.translate("command.player-not-found").send(p);
                return;
            }

            home = getPlace(owner, args[0]);

            if (home.isClosed() && !p.hasPermission("places.admin")) {
                lang.translate("no-permission").send(p);
                return;
            }
        }

        if (home == null) {
            lang.translate("home.not-found").send(p);
            return;
        }

        plugin.getTeleporter().start(p, home);
    }

    private Place getPlace(UUID owner, String name) {
        return plugin.getPlaceManager().getContainer(owner).getPlace(name);
    }
}
