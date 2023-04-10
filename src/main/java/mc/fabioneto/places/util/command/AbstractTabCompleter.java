package mc.fabioneto.places.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractTabCompleter<T extends JavaPlugin> implements TabCompleter {

    protected final T plugin;

    public AbstractTabCompleter(T plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public void register(String cmd) {
        plugin.getCommand(cmd).setTabCompleter(this);
    }

    public void register(String... cmds) {
        for (String cmd : cmds) {
            register(cmd);
        }
    }

    @Override
    public final @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
                                                      @NotNull Command command,
                                                      @NotNull String label,
                                                      @NotNull String[] args) {
        return onTabComplete(commandSender, label, args);
    }

    public abstract List<String> onTabComplete(CommandSender sender, String label, String[] args);
}
