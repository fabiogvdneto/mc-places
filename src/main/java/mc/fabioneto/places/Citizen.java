package mc.fabioneto.places;

import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

public interface Citizen {

    UUID getUID();

    Collection<Place> getPlaces();

    Place getPlace(String id);

    Place createPlace(String name, Location location);

    boolean removePlace(String id);

    boolean ownsPlace(String id);

}
