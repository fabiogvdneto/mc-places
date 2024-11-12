package me.fabiogvdneto.places;

import me.fabiogvdneto.places.command.*;
import me.fabiogvdneto.places.common.teleporter.Teleportation;
import me.fabiogvdneto.places.common.teleporter.Teleporter;
import me.fabiogvdneto.places.model.Place;
import me.fabiogvdneto.places.model.UserManager;
import me.fabiogvdneto.places.model.WarpManager;
import me.fabiogvdneto.places.module.SettingsModule;
import me.fabiogvdneto.places.module.TeleportationModule;
import me.fabiogvdneto.places.module.TranslationModule;
import me.fabiogvdneto.places.module.user.UserModule;
import me.fabiogvdneto.places.module.warp.WarpModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlacesPlugin extends JavaPlugin {

    public static PlacesPlugin INSTANCE;

    private final WarpModule warps = new WarpModule(this);
    private final UserModule users = new UserModule(this);
    private final SettingsModule settings = new SettingsModule(this);
    private final TranslationModule messages = new TranslationModule(this);
    private final TeleportationModule teleportations = new TeleportationModule(this);

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
        new CommandSpawn(this).inject("spawn");
        new CommandWarp(this).inject("warp");
        new CommandWarps(this).inject("warps");
        new CommandDelwarp(this).inject("delwarp");
        new CommandSetwarp(this).inject("setwarp");
        new CommandHome(this).inject("home");
        new CommandHomes(this).inject("homes");
        new CommandSethome(this).inject("sethome");
        new CommandDelhome(this).inject("delhome");
        new CommandTpa(this).inject("tpa");
        new CommandTphere(this).inject("tphere");
        new CommandTpaccept(this).inject("tpaccept");
        new CommandTpdeny(this).inject("tpdeny");
        new CommandTpcancel(this).inject("tpcancel");
        new CommandBack(this).inject("back");
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

    public TranslationModule getMessages() {
        return messages;
    }

    public SettingsModule getSettings() {
        return settings;
    }

    /* ---- Teleportation ---- */

    private void warmup(Teleportation instance, int delay) {
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
                messages.teleportationCountdown(task.getRecipient(), task.getCounter());
            }
        }).withDelay(delay).begin();
    }

    public void teleport(Player player, Place dest) {
        messages.teleportationStarted(player, dest.getName());

        int delay = settings.getTeleporterDelay(player);

        if (delay <= 0) {
            player.teleport(dest.getLocation());
            return;
        }

        warmup(teleportations.create(player, dest::getLocation), delay);
    }

    public void teleport(Player player, Player dest) {
        messages.teleportationStarted(player, dest.getName());

        int delay = settings.getTeleporterDelayForTpa(player);

        if (delay <= 0) {
            player.teleport(dest.getLocation());
            return;
        }

        Teleportation instance = teleportations.create(player, () -> {
            if (player.isOnline()) {
                if (dest.isOnline()) {
                    return dest.getLocation();
                }
                messages.teleportationCancelled(player);
            }
            return null;
        });

        warmup(instance, delay);
    }

    public void teleportBack(Player player) throws IllegalStateException {
        Location prev = teleportations.back(player);

        if (prev == null) throw new IllegalStateException("cannot go back");

        int delay = settings.getTeleporterDelay(player);

        if (delay <= 0) {
            player.teleport(prev);
            return;
        }

        warmup(teleportations.create(player, prev), delay);
    }
}
