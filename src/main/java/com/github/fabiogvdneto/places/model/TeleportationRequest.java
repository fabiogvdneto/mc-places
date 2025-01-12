package com.github.fabiogvdneto.places.model;

import com.github.fabiogvdneto.places.model.exception.TeleportationRequestClosedException;

import java.util.UUID;

public interface TeleportationRequest {

    UUID getSender();

    UUID getReceiver();

    long getExpiryTime();

    boolean hasExpired();

    State getState();

    void cancel() throws TeleportationRequestClosedException;

    void accept() throws TeleportationRequestClosedException;

    void deny() throws TeleportationRequestClosedException;

    enum State {
        OPEN, IGNORED, ACCEPTED, DENIED;
    }
}
