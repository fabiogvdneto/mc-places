package mc.fabioneto.places.data;

import java.io.File;
import java.util.UUID;

public interface PlaceDatabase {

    PlaceContainer getContainer(UUID uid);

    void load(File dir);

    void save(File dir);

    long getTimeToLive();

    void setTimeToLive(long millis);

}
