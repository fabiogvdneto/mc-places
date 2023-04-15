package mc.fabioneto.places.util.place;

import java.io.File;
import java.util.UUID;

public interface PlaceManager {

    PlaceContainer getContainer(UUID uid);

    void load(File dir);

    void save(File dir);

    long getTimeToLive();

    void setTimeToLive(long millis);

}
