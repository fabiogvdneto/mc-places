package com.github.fabiogvdneto.places.model;

import org.bukkit.Location;

public interface Place {

    String getName();

    Location getLocation();

    boolean isClosed();

    void setClosed(boolean closed);

}
