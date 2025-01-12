package com.github.fabiogvdneto.places.model;

import com.github.fabiogvdneto.places.model.exception.WarpAlreadyExistsException;
import com.github.fabiogvdneto.places.model.exception.WarpNotFoundException;
import org.bukkit.Location;

import java.util.Collection;

public interface WarpManager {

    Collection<Place> getAll();

    Place get(String name) throws WarpNotFoundException;

    Place create(String name, Location location) throws WarpAlreadyExistsException;

    void delete(String name) throws WarpNotFoundException;

}
