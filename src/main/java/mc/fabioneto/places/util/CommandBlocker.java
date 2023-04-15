package mc.fabioneto.places.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandBlocker implements Listener {

    private Predicate<Player> filter = (p -> true);
    private Consumer<Player> onBlock = (p -> {});

    private boolean white;
    private Set<String> list = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        if (!filter.test(p)) return;

        String cmd = e.getMessage().substring(1);

        if (list.contains(cmd) != white) {
            e.setCancelled(true);
            onBlock.accept(p);
        }
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public Set<String> getList() {
        return list;
    }

    public void setFilter(Predicate<Player> filter) {
        this.filter = Objects.requireNonNull(filter);
    }

    public void onBlock(Consumer<Player> onBlock) {
        this.onBlock = Objects.requireNonNull(onBlock);
    }
}
