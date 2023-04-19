package mc.fabioneto.places.command;

import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetwarpCommandExecutor extends AbstractCommandExecutor<PlacesPlugin> {

    public SetwarpCommandExecutor(PlacesPlugin plugin, Language language) {
        super(plugin, language);
    }

    @Override
    public Message onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return message("command.players-only");
        }

        if (!p.hasPermission("warps.command.setwarp")) {
            return message("command.no-permission");
        }

        if (args.length == 0) {
            return message("command.usage.setwarp");
        }

        Place place = plugin.getWarpContainer().createPlace(args[0], p.getLocation());

        return message((place == null) ? "warp.already-exists" : "warp.set");
    }
}
