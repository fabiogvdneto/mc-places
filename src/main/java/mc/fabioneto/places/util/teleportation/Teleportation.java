package mc.fabioneto.places.util.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Teleportation {

    Player getPlayer();

    Location getDestination();

    void start() throws IllegalStateException;

    void cancel() throws IllegalStateException;

    boolean isCancelled();

    int getCounter();

    void setCounter(int seconds);

    void updateCounter(int seconds);

    void addCallback(Consumer<Teleportation> callback);

}
