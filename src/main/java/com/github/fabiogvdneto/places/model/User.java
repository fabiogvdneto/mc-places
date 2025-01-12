package com.github.fabiogvdneto.places.model;

import com.github.fabiogvdneto.places.model.exception.HomeAlreadyExistsException;
import com.github.fabiogvdneto.places.model.exception.HomeNotFoundException;
import com.github.fabiogvdneto.places.model.exception.TeleportationRequestAlreadyExistsException;
import com.github.fabiogvdneto.places.model.exception.TeleportationRequestNotFoundException;
import org.bukkit.Location;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public interface User {

    UUID getUID();

    Collection<Home> getHomes();

    Home getHome(String name)
            throws HomeNotFoundException;

    Home createHome(String name, Location location)
            throws HomeAlreadyExistsException;

    void deleteHome(String name)
            throws HomeNotFoundException;

    Collection<TeleportationRequest> getTeleportationRequests();

    TeleportationRequest getTeleportationRequest(UUID sender)
            throws TeleportationRequestNotFoundException;

    TeleportationRequest createTeleportationRequest(UUID sender, Duration duration)
            throws TeleportationRequestAlreadyExistsException;
}
