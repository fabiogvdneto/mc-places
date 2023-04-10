package mc.fabioneto.places.util.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface Teleporter {

    void teleport(Player player, Location dest);

    boolean cancel(UUID player);

    int getMaxDelay();

    void setMaxDelay(int seconds);

    boolean isMovementAllowed();

    void setMovementAllowed(boolean allowed);

    boolean isDamageAllowed();

    void setDamageAllowed(boolean allowed);

    void modCommandBlocker(int mode, Collection<String> list);

}
