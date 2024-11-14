package me.fabiogvdneto.places.common.command;

import me.fabiogvdneto.places.common.command.exception.IllegalArgumentException;
import me.fabiogvdneto.places.common.command.exception.IllegalSenderException;
import me.fabiogvdneto.places.common.command.exception.PermissionRequiredException;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class CommandHandler<P extends JavaPlugin> implements CommandExecutor, TabCompleter {

    protected final P plugin;

    public CommandHandler(P plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /* ---- Registration ---- */

    public final void inject(String label) {
        PluginCommand command = plugin.getCommand(label);

        if (command != null) {
            inject(command);
        }
    }

    public final void inject(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /* ---- Template Methods ---- */

    @Override
    public final boolean onCommand(@NotNull CommandSender sender,
                                   @NotNull Command command,
                                   @NotNull String label,
                                   @NotNull String[] args) {
        execute(sender, command, label, args);
        return true;
    }

    @Override @Nullable
    public final List<String> onTabComplete(@NotNull CommandSender sender,
                                            @NotNull Command command,
                                            @NotNull String label,
                                            @NotNull String[] args) {
        return complete(sender, command, label, args);
    }

    /* ---- Operations ---- */

    public abstract void execute(CommandSender sender, Command cmd, String label, String[] args);

    public List<String> complete(CommandSender sender, Command cmd, String label, String[] args) {
        // Default behaviour: completes with nothing.
        return Collections.emptyList();
    }

    /* ---- Validations ---- */

    public final void requirePlayer(CommandSender sender) throws IllegalSenderException {
        if (!(sender instanceof Player))
            throw new IllegalSenderException();
    }

    public final void requireArguments(String[] args, int minimumLength) throws IllegalArgumentException {
        if (args.length < minimumLength)
            throw new IllegalArgumentException(-1);
    }

    public final void requirePermission(CommandSender sender, String permission) throws PermissionRequiredException {
        if (!sender.hasPermission(permission))
            throw new PermissionRequiredException(permission);
    }

    public final int parseInt(String[] args, int index) throws IllegalArgumentException {
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(index);
        }
    }

    public final long parseLong(String[] args, int index) throws IllegalArgumentException {
        try {
            return Long.parseLong(args[index]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(index);
        }
    }

    public final Player parsePlayer(String[] args, int index) throws IllegalArgumentException {
        Player player = Bukkit.getPlayer(args[index]);

        if (player == null)
            throw new IllegalArgumentException(index);

        return player;
    }
}
