package mc.fabioneto.places;

import java.util.UUID;

public interface PlaceManager {

    Citizen getCitizen(UUID uid);

    void load();

    void save();

    void autosave(int minutes);

    long getTimeToLive();

    void setTimeToLive(long millis);

}
