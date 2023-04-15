package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetwarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public SetwarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            translate("command.players-only").send(sender);
            return;
        }

        if (!p.hasPermission("warps.command.setwarp")) {
            translate("command.no-permission").send(p);
            return;
        }

        if (args.length == 0) {
            translate("command.usage.setwarp").send(p);
            return;
        }

        Place place = plugin.getPlaceManager().getContainer(null).createPlace(args[0], p.getLocation());

        if (place == null) {
            translate("warp.already-exists").send(p);
            return;
        }

        translate("warp.set").send(p);
    }
}
