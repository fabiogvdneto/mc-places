package mc.fabioneto.places.util.teleportation;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;

public class DamageDetector implements Consumer<Teleportation> {

    private final Consumer<Teleportation> callback;
    private double healthTracker;

    public DamageDetector(Consumer<Teleportation> callback) {
        this.callback = Objects.requireNonNull(callback);
    }

    public static DamageDetector cancelWithFeedback(String msg) {
        return new DamageDetector(t -> {
            t.cancel();
            t.getPlayer().sendMessage(msg);
        });
    }

    @Override
    public void accept(Teleportation t) {
        Player p = t.getPlayer();
        double health = p.getHealth();

        if (health < healthTracker) {
            callback.accept(t);
        }

        healthTracker = health;
    }
}
