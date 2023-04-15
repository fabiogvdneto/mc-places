package mc.fabioneto.places.command;

import mc.fabioneto.places.util.place.Place;
import mc.fabioneto.places.PlacesPlugin;
import mc.fabioneto.places.util.command.AbstractCommandExecutor;
import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.teleportation.Teleportation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.IntStream;

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

        Place warp = plugin.getPlaceManager().getContainer(null).getPlace(args[0]);

        if (isNotAvailable(p, warp)) {
            lang.translate("warp.no-permission").send(p);
            return;
        }

        plugin.getTeleporter().start(p, warp);
    }

    private boolean isNotAvailable(Player p, Place warp) {
        return warp.isClosed() && !p.hasPermission("places.warp." + warp.getName());
    }
}
