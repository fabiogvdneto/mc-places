package com.github.fabiogvdneto.places.model.exception;

import com.github.fabiogvdneto.places.model.TeleportationRequest;

public class TeleportationRequestAlreadyExistsException extends Exception {

    private final TeleportationRequest value;

    public TeleportationRequestAlreadyExistsException(TeleportationRequest value) {
        this.value = value;
    }

    public TeleportationRequest getValue() {
        return value;
    }
}
