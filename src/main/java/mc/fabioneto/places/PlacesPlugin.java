package mc.fabioneto.places;

import mc.fabioneto.places.command.*;
import mc.fabioneto.places.data.JsonPlaceDatabase;
import mc.fabioneto.places.data.Place;
import mc.fabioneto.places.data.PlaceContainer;
import mc.fabioneto.places.data.PlaceDatabase;
import mc.fabioneto.places.util.CommandBlocker;
import mc.fabioneto.places.util.PlayerDatabase;
import mc.fabioneto.places.util.lang.PluginLanguage;
import mc.fabioneto.places.util.teleportation.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;
import java.util.stream.IntStream;

public final class PlacesPlugin extends JavaPlugin {

    /* ---- Global Variables ---- */

    private final PluginLanguage language = new PluginLanguage(this, "languages" + File.separatorChar);
    private final PlaceDatabase placeDatabase = new JsonPlaceDatabase();
    private final PlayerDatabase playerDatabase = new PlayerDatabase(getLogger());
    private final Teleporter teleporter = new PluginTeleporter(this);
    private final CommandBlocker cmdBlocker = new CommandBlocker();

    private File keysFile;
    private File placesFolder;

    private BukkitTask autosave;

    /* ---- Getters ---- */

    public PluginLanguage getLanguage() {
        return language;
    }

    /* ---- On Enable ---- */

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();

        createFiles();
        loadPlayerFetcher();
        loadManager();
        loadLanguage();
        loadCommandBlocker();
        registerCommands();

        autosave(getConfig().getInt("autosave"));
    }

    private void createFiles() {
        this.keysFile = newFile("keys.json");
        this.placesFolder = newFile("places");

        placesFolder.mkdirs();
    }

    private void loadPlayerFetcher() {
        playerDatabase.load(keysFile);

        getServer().getPluginManager().registerEvents(playerDatabase, this);
    }

    private void loadManager() {
        placeDatabase.load(placesFolder);
        placeDatabase.setTimeToLive(getConfig().getInt("ttl"));
    }

    private void loadLanguage() {
        language.load(getConfig().getString("language"));
    }

    private void loadCommandBlocker() {
        int mode = getConfig().getInt("cmd-blocker.mode");

        if (mode == 0) return;

        cmdBlocker.setWhite(mode > 0);
        cmdBlocker.getList().addAll(getConfig().getStringList("cmd-blocker.list"));
        cmdBlocker.setFilter(p -> (teleporter.get(p.getUniqueId()) != null));
        cmdBlocker.onBlock(p -> language.translate("teleportation.cmd-blocked").send(p));

        getServer().getPluginManager().registerEvents(cmdBlocker, this);
    }

    private void registerCommands() {
        new WarpCommandExecutor(this, language).register("warp");
        new WarpsCommandExecutor(this, language).register("warps");
        new SetwarpCommandExecutor(this, language).register("setwarp");
        new DelwarpCommandExecutor(this, language).register("delwarp");

        new HomeCommandExecutor(this, language).register("home");
        new HomesCommandExecutor(this, language).register("homes");
        new SethomeCommandExecutor(this, language).register("sethome");
        new DelhomeCommandExecutor(this, language).register("delhome");

        new WarpTabCompleter(this).register("warp", "delwarp");
        new HomeTabCompleter(this).register("home", "delhome");
    }

    private void autosave(int minutes) {
        if (autosave != null) {
            autosave.cancel();
        }

        if (minutes <= 0) {
            autosave = null;
            return;
        }

        long ticks = (long) minutes * 60 * 20;

        this.autosave = Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                () -> placeDatabase.save(placesFolder), ticks, ticks);
    }

    /* ---- On Disable ---- */

    @Override
    public void onDisable() {
        autosave(0);

        placeDatabase.save(placesFolder);
        playerDatabase.save(keysFile);

        placeDatabase.setTimeToLive(0);
    }

    /* ---- Utils ---- */

    private File newFile(String name) {
        return new File(getDataFolder(), name);
    }

    /* ---- Places ---- */

    public PlaceContainer getContainer(UUID owner) {
        return placeDatabase.getContainer(owner);
    }

    public PlaceContainer getHomeContainer(UUID owner) {
        return (owner == null) ? null : getContainer(owner);
    }

    public PlaceContainer getHomeContainer(String owner) {
        return getHomeContainer(playerDatabase.fetchID(owner));
    }

    public PlaceContainer getWarpContainer() {
        return getContainer(null);
    }

    public boolean hasWarpPermission(CommandSender sender, String warp) {
        return sender.hasPermission("places.warp." + warp);
    }

    public boolean hasAdminPermission(CommandSender sender) {
        return sender.hasPermission("places.admin");
    }

    public int getHomeLimit(Player player) {
        int limit = getConfig().getInt("max-home-limit");

        while (!player.hasPermission("places.home-limit." + limit) && (--limit > 0)) ;

        return limit;
    }

    /* ---- Teleportation ---- */

    public void teleport(Player player, Place place) {
        int delay = computeTeleportationDelay(player);

        Teleportation teleportation = teleporter.create(player, place.getLocation(), delay);

        if (!isMovementAllowed()) {
            String feedback = language.translate("teleportation.movement-not-allowed").getContent();

            teleportation.addCallback(MovementDetector.cancelWithFeedback(feedback));
        }

        if (!isDamageAllowed()) {
            String feedback = language.translate("teleportation.damage-not-allowed").getContent();

            teleportation.addCallback(DamageDetector.cancelWithFeedback(feedback));
        }

        teleportation.addCallback(t -> {
            if (t.getCounter() == 0) {
                language.translate("teleportation.finished").format(place.getName()).send(t.getPlayer());
            } else {
                language.translate("teleportation.countdown").format(t.getCounter()).send(t.getPlayer());
            }
        });

        teleportation.start();
    }

    private int computeTeleportationDelay(Player player) {
        int max = getConfig().getInt("max-delay");

        return IntStream.range(0, max).filter(i -> player.hasPermission("places.delay." + i)).findFirst().orElse(max);
    }

    private boolean isMovementAllowed() {
        return getConfig().getBoolean("movement-allowed");
    }

    private boolean isDamageAllowed() {
        return getConfig().getBoolean("damage-allowed");
    }
}
