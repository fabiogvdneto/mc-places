package me.fabiogvdneto.places.service.warp;

import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.repository.data.LocationData;
import me.fabiogvdneto.places.repository.data.WarpData;
import org.bukkit.Location;

class StandardWarp implements Place {

    private final String name;
    private final Location location;
    private boolean closed;

    public StandardWarp(WarpData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    public StandardWarp(String name, Location location) {
        this.name = name;
        this.location = location;
        this.closed = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public WarpData data() {
        return new WarpData(name, new LocationData(location), closed);
    }
}
