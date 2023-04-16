package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DelhomeCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public DelhomeCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            lang.translate("command.players-only").send(sender);
            return;
        }

        if (args.length == 0) {
            lang.translate("command.usage.delhome").send(sender);
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

        if (!plugin.getPlaceManager().getContainer(owner).removePlace(args[0])) {
            lang.translate("home.not-found").send(p);
            return;
        }

        lang.translate("home.set").send(p);
    }
}
