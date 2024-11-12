package me.fabiogvdneto.places.repository.data;

import java.io.Serializable;

public record HomeData(
        String name,
        LocationData location,
        boolean closed
) implements Serializable { }
