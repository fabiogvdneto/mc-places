package me.fabiogvdneto.places.model.exception;

import me.fabiogvdneto.places.model.TeleportationRequest;

public class TeleportationRequestAlreadyExistsException extends Exception {

    private final TeleportationRequest value;

    public TeleportationRequestAlreadyExistsException(TeleportationRequest value) {
        this.value = value;
    }

    public TeleportationRequest getValue() {
        return value;
    }
}
