package mc.fabioneto.places;

import mc.fabioneto.places.command.*;
import mc.fabioneto.places.util.CommandBlocker;
import mc.fabioneto.places.util.PlayerDatabase;
import mc.fabioneto.places.util.lang.PluginLanguage;
import mc.fabioneto.places.util.place.JsonPlaceManager;
import mc.fabioneto.places.util.place.PlaceManager;
import mc.fabioneto.places.util.teleportation.PluginTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class PlacesPlugin extends JavaPlugin {

    /* ---- Global Variables ---- */

    private final PluginLanguage lang = new PluginLanguage(this, "languages" + File.separatorChar);
    private final PlaceManager manager = new JsonPlaceManager();
    private final PlayerDatabase playerDatabase = new PlayerDatabase(getLogger());
    private final PlacesTeleporter teleporter = new PlacesTeleporter(this);
    private final CommandBlocker cmdBlocker = new CommandBlocker();

    private File keysFile;
    private File placesFolder;

    private BukkitTask autosave;

    /* ---- Getters ---- */

    public PluginLanguage getLanguage() {
        return lang;
    }

    public PlaceManager getPlaceManager() {
        return manager;
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }

    public PlacesTeleporter getTeleporter() {
        return teleporter;
    }

    public CommandBlocker getCommandBlocker() {
        return cmdBlocker;
    }

    /* ---- On Load ---- */

    @Override
    public void onLoad() {
        Places.setPlugin(this);
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
        manager.load(placesFolder);
        manager.setTimeToLive(getConfig().getInt("ttl"));
    }

    private void loadLanguage() {
        lang.load(getConfig().getString("language"));
    }

    private void loadCommandBlocker() {
        int mode = getConfig().getInt("cmd-blocker.mode");

        if (mode == 0) return;

        cmdBlocker.setWhite(mode > 0);
        cmdBlocker.getList().addAll(getConfig().getStringList("cmd-blocker.list"));
        cmdBlocker.setFilter(p -> (teleporter.get(p.getUniqueId()) != null));
        cmdBlocker.onBlock(p -> lang.translate("teleportation.cmd-blocked").send(p));

        getServer().getPluginManager().registerEvents(cmdBlocker, this);
    }

    private void registerCommands() {
        new WarpCommandExecutor(this, lang).register("warp");
        new WarpsCommandExecutor(this, lang).register("warps");
        new SetwarpCommandExecutor(this, lang).register("setwarp");
        new DelwarpCommandExecutor(this, lang).register("delwarp");

        new HomeCommandExecutor(this, lang).register("home");
        new HomesCommandExecutor(this, lang).register("homes");
        new SethomeCommandExecutor(this, lang).register("sethome");
        new DelhomeCommandExecutor(this, lang).register("delhome");

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
                () -> manager.save(placesFolder),
        ticks, ticks);
    }

    /* ---- On Disable ---- */

    @Override
    public void onDisable() {
        autosave(0);

        manager.save(placesFolder);
        playerDatabase.save(keysFile);

        manager.setTimeToLive(0);
    }

    /* ---- Utils ---- */

    private File newFile(String name) {
        return new File(getDataFolder(), name);
    }
}
