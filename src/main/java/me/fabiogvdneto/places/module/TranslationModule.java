package me.fabiogvdneto.places.module;

import me.fabiogvdneto.places.PlacesModule;
import me.fabiogvdneto.places.PlacesPlugin;
import me.fabiogvdneto.places.common.i18n.PluginTranslator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Collection;

public final class TranslationModule implements PlacesModule {

    private final PlacesPlugin plugin;
    private final PluginTranslator translator;

    public TranslationModule(PlacesPlugin plugin) {
        this.plugin = plugin;
        this.translator = new PluginTranslator();
    }

    public void enable() {
        try {
            translator.loadTranslations(plugin, "en");
            translator.loadTranslations(plugin, plugin.getSettings().getLanguage());
        } catch (IOException e) {
            plugin.getLogger().warning("An error occurred while trying to load translations.");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void disable() {
        translator.clearTranslations();
    }

    private Component message(String key) {
        String translation = translator.get(key);
        return MiniMessage.miniMessage().deserialize(translation);
    }

    private void message(String key, Audience output) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation));
    }

    private void message(String key, Audience output, TagResolver resolver) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation, resolver));
    }

    private void message(String key, Audience output, TagResolver... resolvers) {
        String translation = translator.get(key);
        output.sendMessage(MiniMessage.miniMessage().deserialize(translation, resolvers));
    }

    /* ---- Misc ---- */

    public void playersOnly(Audience target) {
        message("command.players-only", target);
    }

    public void playerNotFound(Audience target, String playerName) {
        message("command.player-not-found", target, Placeholder.unparsed("player", playerName));
    }

    public void permissionRequired(Audience target) {
        message("command.permission-required", target);
    }

    public void commandUsage(Audience target, String commandName) {
        message("command.usage." + commandName, target);
    }

    /* ---- Teleporter ---- */

    public void movementNotAllowedWhileTeleporting(Audience target) {
        message("teleporter.movement-not-allowed", target);
    }

    public void damageNotAllowedWhileTeleporting(Audience target) {
        message("teleporter.damage-not-allowed", target);
    }

    public void teleportationCancelled(Audience target) {
        message("teleporter.cancelled", target);
    }

    public void teleportationStarted(Audience target, String destName) {
        message("teleporter.started", target, Placeholder.unparsed("destination", destName));
    }

    public void teleportationCountdown(Audience target, int remaining) {
        message("teleporter.countdown", target, Placeholder.unparsed("seconds", Integer.toString(remaining)));
    }

    public void teleportedSuccessfully(Audience target) {
        message("teleporter.success", target);
    }

    public void teleportationNotFound(Audience target) {
        message("teleporter.player-not-found", target);
    }

    public void cannotTeleportBack(Audience target) {
        message("teleporter.cannot-go-back", target);
    }

    /* ---- Teleportation Requests ---- */

    public void teleportationRequestSent(Audience target, String receiver) {
        message("tpask.sent", target, Placeholder.unparsed("receiver", receiver));
    }

    public void teleportationRequestReceived(Audience target, String sender) {
        message("tpask.received", target, Placeholder.parsed("sender", sender));
    }

    public void teleportationRequestExpired(Audience target, String sender) {
        message("tpask.expired", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestDenied(Audience target, String sender) {
        message("tpask.denied", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestAccepted(Audience target, String sender) {
        message("tpask.accepted", target, Placeholder.unparsed("sender", sender));
    }

    public void teleportationRequestNotFound(Audience target) {
        message("tpask.not-found", target);
    }

    public void teleportationRequestPending(Audience target) {
        message("tpask.pending", target);
    }

    public void teleportationRequestCooldown(Audience target) {
        message("tpask.cooldown", target);
    }

    public void teleportationRequestIgnored(Audience target) {
        message("tpask.ignored", target);
    }

    public void teleportationRequestYourself(Audience target) {
        message("tpask.yourself", target);
    }

    /* ---- Warps ---- */

    public void warpList(CommandSender target, Collection<String> warps) {
        if (warps.isEmpty()) {
            message("warp.list.empty", target);
            return;
        }

        Component sep = message("warp.list.separator");
        Component list = warps.stream().map(Component::text).collect(Component.toComponent(sep));

        message("warp.list.base", target, Placeholder.component("list", list));
    }

    public void warpSet(Audience target) {
        message("warp.set", target);
    }

    public void warpDeleted(Audience target) {
        message("warp.deleted", target);
    }

    public void warpNotFound(Audience target) {
        message("warp.not-found", target);
    }

    public void warpAlreadyExists(Audience target) {
        message("warp.already-exists", target);
    }

    /* ---- Homes ---- */

    public void homeListEmpty(Audience target) {
        message("home.list.empty", target);
    }

    public void homeList(Audience target, Collection<String> homes) {
        if (homes.isEmpty()) {
            homeListEmpty(target);
            return;
        }

        Component sep = message("home.list.separator");
        Component list = homes.stream().map(Component::text).collect(Component.toComponent(sep));

        message("home.list.base", target, Placeholder.component("list", list));
    }

    public void homeSet(Audience target) {
        message("home.set", target);
    }

    public void homeDeleted(Audience target) {
        message("home.deleted", target);
    }

    public void homeNotFound(Audience target) {
        message("home.not-found", target);
    }

    public void homeAlreadyExists(Audience target) {
        message("home.already-exists", target);
    }

    public void homeLimitReached(Audience target, int count, int limit) {
        message("home.limit-reached", target,
                Placeholder.unparsed("count", Integer.toString(count)),
                Placeholder.unparsed("limit", Integer.toString(limit)));
    }

    /* ---- Spawn ---- */

    public void spawnNotFound(Audience target) {
        message("spawn.undefined", target);
    }
}
