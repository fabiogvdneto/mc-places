package com.github.fabiogvdneto.places.service.warp;

import com.github.fabiogvdneto.places.model.Place;
import com.github.fabiogvdneto.places.repository.data.LocationData;
import com.github.fabiogvdneto.places.repository.data.WarpData;
import org.bukkit.Location;

class SimpleWarp implements Place {

    private final String name;
    private final Location location;
    private boolean closed;

    public SimpleWarp(WarpData data) {
        this.name = data.name();
        this.location = data.location().bukkit();
        this.closed = data.closed();
    }

    public SimpleWarp(String name, Location location) {
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
