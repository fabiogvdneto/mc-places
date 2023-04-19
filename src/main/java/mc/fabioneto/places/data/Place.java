package mc.fabioneto.places.data;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Place {

    String getName();

    Location getLocation();

    boolean isClosed();

    void setClosed(boolean closed);

    void tphere(Entity entity);

}
