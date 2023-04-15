package mc.fabioneto.places.util.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface Teleporter {

    Collection<Teleportation> getAll();

    Teleportation get(UUID id);

    Teleportation create(Player player, Location dest);

    Teleportation create(Player player, Location dest, int delay);

}
