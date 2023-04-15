package mc.fabioneto.places.util.place;

import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

public interface PlaceContainer {

    UUID getOwner();

    Collection<Place> getPlaces();

    Place getPlace(String id);

    Place createPlace(String name, Location location);

    boolean removePlace(String id);

    boolean ownsPlace(String id);

}
