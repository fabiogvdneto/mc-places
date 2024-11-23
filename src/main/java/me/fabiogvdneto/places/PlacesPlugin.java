package me.fabiogvdneto.places;

import me.fabiogvdneto.places.command.*;
import me.fabiogvdneto.places.common.teleporter.Teleportation;
import me.fabiogvdneto.places.common.teleporter.Teleporter;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.UserManager;
import me.fabiogvdneto.places.model.WarpManager;
import me.fabiogvdneto.places.service.ConfigurationService;
import me.fabiogvdneto.places.service.TeleportationService;
import me.fabiogvdneto.places.service.TranslationService;
import me.fabiogvdneto.places.service.user.UserService;
import me.fabiogvdneto.places.service.warp.WarpService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

public final class PlacesPlugin extends JavaPlugin {

    public static PlacesPlugin INSTANCE;

    private final WarpService warps = new WarpService(this);
    private final UserService users = new UserService(this);
    private final ConfigurationService settings = new ConfigurationService(this);
    private final TranslationService messages = new TranslationService(this);
    private final TeleportationService teleportations = new TeleportationService(this);

    /* ---- Bootstrap ---- */

    @Override
    public void onEnable() {
        PlacesPlugin.INSTANCE = this;

        messages.enable();
        settings.enable();
        teleportations.enable();

        warps.enable();
        users.enable();

        registerCommands();
    }

    private void registerCommands() {
        new CommandSpawn(this).registerAs("spawn");
        new CommandWarp(this).registerAs("warp");
        new CommandWarps(this).registerAs("warps");
        new CommandDelwarp(this).registerAs("delwarp");
        new CommandSetwarp(this).registerAs("setwarp");
        new CommandHome(this).registerAs("home");
        new CommandHomes(this).registerAs("homes");
        new CommandSethome(this).registerAs("sethome");
        new CommandDelhome(this).registerAs("delhome");
        new CommandTpa(this).registerAs("tpa");
        new CommandTphere(this).registerAs("tphere");
        new CommandTpaccept(this).registerAs("tpaccept");
        new CommandTpdeny(this).registerAs("tpdeny");
        new CommandTpcancel(this).registerAs("tpcancel");
        new CommandBack(this).registerAs("back");
    }

    @Override
    public void onDisable() {
        users.disable();
        warps.disable();

        teleportations.disable();
        messages.disable();
        settings.disable();

        PlacesPlugin.INSTANCE = null;
    }

    /* ---- Modules ---- */

    public WarpManager getWarps() {
        return warps;
    }

    public UserManager getUsers() {
        return users;
    }

    public Teleporter getTeleporter() {
        return teleportations;
    }

    public TranslationService getMessages() {
        return messages;
    }

    public ConfigurationService getSettings() {
        return settings;
    }

    /* ---- Teleportation ---- */

    private void teleport(Player player, Supplier<Location> destSupplier, int delay) {
        Teleportation instance = teleportations.create(player, destSupplier);

        if (delay > 0) {
            if (!settings.isMovementAllowedWhileTeleporting()) {
                instance.onMovement(task -> {
                    task.cancel();
                    messages.movementNotAllowedWhileTeleporting(task.getRecipient());
                });
            }

            if (!settings.isDamageAllowedWhileTeleporting()) {
                instance.onDamage(task -> {
                    task.cancel();
                    messages.damageNotAllowedWhileTeleporting(task.getRecipient());
                });
            }

            instance.onCountdown(task -> {
                if (task.getCounter() == 0) {
                    messages.teleportedSuccessfully(task.getRecipient());
                } else {
                    messages.teleportationCountdown(task.getRecipient(), Integer.toString(task.getCounter()));
                }
            });
        } else {
            instance.onCountdown(task -> messages.teleportedSuccessfully(task.getRecipient()));
        }

        instance.withDelay(delay).begin();
    }

    public void teleport(Player player, Place dest) {
        teleport(player, dest::getLocation, settings.getTeleporterDelay(player));
    }

    public void teleport(Player player, Player dest) {
        Supplier<Location> destSupplier = () -> {
            if (player.isOnline()) {
                if (dest.isOnline()) {
                    return dest.getLocation();
                }
                messages.teleportationCancelled(player);
            }
            return null;
        };

        teleport(player, destSupplier, settings.getTeleporterDelayForTpa(player));
    }

    public void teleportBack(Player player) throws IllegalStateException {
        Location prev = teleportations.back(player);

        if (prev == null) throw new IllegalStateException("cannot go back");

        teleport(player, () -> prev, settings.getTeleporterDelay(player));
    }
}
