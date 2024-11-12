package me.fabiogvdneto.places.common.teleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Teleportation {

    Player getRecipient();

    Location getDestination();

    int getDelay();

    int getCounter();

    void begin() throws IllegalStateException;

    void cancel() throws IllegalStateException;

    Teleportation withDelay(int delay);

    Teleportation onCountdown(Consumer<Teleportation> callback);

    default Teleportation onMovement(Consumer<Teleportation> callback) {
        onCountdown(new Consumer<>() {
            private static final double THRESHOLD = 0.02;
            private Location location;

            @Override
            public void accept(Teleportation teleportation) {
                Location newLocation = teleportation.getRecipient().getLocation();

                if (location == null) {
                    location = newLocation.clone();
                } else if (newLocation.distanceSquared(location) > THRESHOLD) {
                    callback.accept(teleportation);
                }
            }
        });
        return this;
    }

    default Teleportation onDamage(Consumer<Teleportation> callback) {
        onCountdown(new Consumer<>() {
            private double healthTracker;

            @Override
            public void accept(Teleportation teleportation) {
                double health = teleportation.getRecipient().getHealth();

                if (health < healthTracker) {
                    callback.accept(teleportation);
                }

                healthTracker = health;
            }
        });
        return this;
    }

}
