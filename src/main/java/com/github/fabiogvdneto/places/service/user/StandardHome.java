package com.github.fabiogvdneto.places.service.user;

import com.github.fabiogvdneto.places.model.Home;
import com.github.fabiogvdneto.places.repository.data.HomeData;
import com.github.fabiogvdneto.places.repository.data.LocationData;
import org.bukkit.Location;

class StandardHome implements Home {

    private final String name;
    private final Location location;
    private boolean closed;

    StandardHome(HomeData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    StandardHome(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    HomeData memento() {
        return new HomeData(name, new LocationData(location), closed);
    }
}
