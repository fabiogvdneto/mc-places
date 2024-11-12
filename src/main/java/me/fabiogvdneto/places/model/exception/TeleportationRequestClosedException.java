package me.fabiogvdneto.places.model.exception;

import me.fabiogvdneto.places.model.TeleportationRequest;

public class TeleportationRequestClosedException extends IllegalStateException {

    private final TeleportationRequest.State state;

    public TeleportationRequestClosedException(TeleportationRequest.State state) {
        this.state = state;
    }

    public TeleportationRequest.State getState() {
        return state;
    }
}
