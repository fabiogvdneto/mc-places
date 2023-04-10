package mc.fabioneto.places;

import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import mc.fabioneto.places.util.teleportation.AbstractTeleporter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.stream.IntStream;

public class PlaceTeleporter extends AbstractTeleporter {

    private final Language lang;

    public PlaceTeleporter(Plugin plugin, Language lang) {
        super(plugin);

        this.lang = Objects.requireNonNull(lang);
    }

    @Override
    protected int computeDelay(Player player) {
        int max = getMaxDelay();

        return IntStream.range(0, max)
                .filter(i -> player.hasPermission("places.delay." + i))
                .findFirst().orElse(max);
    }

    @Override
    protected Message getCountdownMessage() {
        return lang.translate("teleportation.countdown");
    }

    @Override
    protected Message getTeleportedMessage() {
        return lang.translate("teleportation.finished");
    }

    @Override
    protected Message getDamageNotAllowedMessage() {
        return lang.translate("teleportation.damage-not-allowed");
    }

    @Override
    protected Message getMovementNotAllowedMessage() {
        return lang.translate("teleportation.movement-not-allowed");
    }
}
