package me.fabiogvdneto.places.model;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface UserManager {

    Collection<User> getAll();

    User getIfCached(UUID userId);

    void fetch(UUID userId, Consumer<User> callback);

}
