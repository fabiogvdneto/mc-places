package mc.fabioneto.places.util.command;

import mc.fabioneto.places.util.lang.Language;
import mc.fabioneto.places.util.lang.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractCommandExecutor<T extends JavaPlugin> implements CommandExecutor {

    protected final T plugin;
    protected final Language lang;

    public AbstractCommandExecutor(T plugin, Language language) {
        this.plugin = Objects.requireNonNull(plugin);
        this.lang = Objects.requireNonNull(language);
    }

    public void register(String cmd) {
        plugin.getCommand(cmd).setExecutor(this);
    }

    public void register(String... cmds) {
        for (String cmd : cmds) {
            register(cmd);
        }
    }

    protected Message translate(String key) {
        return lang.translate(key);
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                                   @NotNull String label, @NotNull String[] args) {
        onCommand(sender, label, args);
        return true;
    }

    public abstract void onCommand(CommandSender sender, String label, String[] args);
}
