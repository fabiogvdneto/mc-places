package mc.fabioneto.places.util.teleportation;

import org.bukkit.Location;

import java.util.Objects;
import java.util.function.Consumer;

public class MovementDetector implements Consumer<Teleportation> {

    private final Consumer<Teleportation> callback;

    private boolean init;
    private int x;
    private int y;
    private int z;

    public MovementDetector(Consumer<Teleportation> callback) {
        this.callback = Objects.requireNonNull(callback);
    }

    public static MovementDetector cancelWithFeedback(String msg) {
        return new MovementDetector(t -> {
            t.cancel();
            t.getPlayer().sendMessage(msg);
        });
    }

    @Override
    public void accept(Teleportation teleportation) {
        Location loc = teleportation.getPlayer().getLocation();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        if (!init) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.init = true;
        } else if ((this.x != x) || (this.y != y) || (this.z != z)) {
            callback.accept(teleportation);
        }
    }
}
