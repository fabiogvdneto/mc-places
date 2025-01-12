package com.github.fabiogvdneto.places.model.exception;

import com.github.fabiogvdneto.places.model.TeleportationRequest;

public class TeleportationRequestClosedException extends IllegalStateException {

    private final TeleportationRequest.State state;

    public TeleportationRequestClosedException(TeleportationRequest.State state) {
        this.state = state;
    }

    public TeleportationRequest.State getState() {
        return state;
    }
}
