package mc.fabioneto.places.command;

import mc.fabioneto.places.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public WarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            translate("command.players-only").send(sender);
            return;
        }

        if (args.length == 0) {
            p.performCommand("warps");
            return;
        }

        Place warp = plugin.getPlaceManager().getCitizen(null).getPlace(args[0]);

        if (warp.isClosed() && !p.hasPermission("places.warp." + warp.getName())) {
            lang.translate("warp.no-permission").send(p);
            return;
        }

        warp.tphere(p);
        lang.translate("teleportation.finished").send(p);
    }
}
