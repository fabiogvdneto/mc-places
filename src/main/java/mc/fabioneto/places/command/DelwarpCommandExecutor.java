package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelwarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public DelwarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            translate("command.players-only").send(sender);
            return;
        }

        if (!p.hasPermission("warps.command.delwarp")) {
            translate("command.no-permission").send(p);
            return;
        }

        if (args.length == 0) {
            translate("command.usage.delwarp").send(p);
            return;
        }
        
        if (!plugin.getPlaceManager().getContainer(null).removePlace(args[0])) {
            translate("warp.not-found").send(p);
            return;
        }

        translate("warp.deleted").send(p);
    }
}
