package com.github.fabiogvdneto.places.repository.data;

import java.io.Serializable;

public record WarpData(
        String name,
        LocationData location,
        boolean closed
) implements Serializable { }
