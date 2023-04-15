package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.teleportation.Teleportation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

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

        UUID owner = (args.length > 1)
                ? plugin.getPlayerDatabase().fetchID(args[1])
                : p.getUniqueId();

        if (owner == null) {
            lang.translate("home.player-not-found").send(p);
            return;
        }

        Place home = plugin.getPlaceManager().getContainer(owner).getPlace(args[0]);

        if (home == null) {
            lang.translate("home.not-found").send(p);
            return;
        }

        plugin.getTeleporter().start(p, home);
    }
}
