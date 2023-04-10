package mc.fabioneto.places;

import mc.fabioneto.places.command.*;
import mc.fabioneto.places.util.PlayerFetcher;
import mc.fabioneto.places.util.Resources;
import mc.fabioneto.places.util.lang.PluginLanguage;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlacesPlugin extends JavaPlugin {

    private PluginLanguage lang;
    private PlaceManager manager;
    private PlayerFetcher playerFetcher;
    private PlaceTeleporter teleporter;

    @Override
    public void onLoad() {
        Places.setPlugin(this);
    }

    @Override
    public void onEnable() {
        Configuration config = Resources.saveAndLoad(this, "settings.yml");

        loadManager(config);
        loadPlayerFetcher();
        loadLanguage(config);
        loadTeleporter(config);
        loadCommands();
    }

    @Override
    public void onDisable() {
        manager.autosave(0);
        manager.setTimeToLive(0);
        manager.save();
        playerFetcher.save(createKeysFile());

        this.manager = null;
        this.lang = null;
        this.playerFetcher = null;
        this.teleporter = null;
    }

    public PlaceManager getPlaceManager() {
        return manager;
    }

    private void loadManager(Configuration config) {
        this.manager = new PluginPlaceManager(this, new File(getDataFolder(), "places"));

        manager.load();
        manager.autosave(config.getInt("autosave"));
        manager.setTimeToLive(config.getInt("ttl"));
    }

    private void loadLanguage(Configuration config) {
        this.lang = new PluginLanguage(this, "languages" + File.separatorChar);

        lang.load(config.getString("language"));
    }

    private void loadCommands() {
        new WarpCommandExecutor(this, lang).register("warp");
        new WarpsCommandExecutor(this, lang).register("warps");
        new SetwarpCommandExecutor(this, lang).register("setwarp");
        new DelwarpCommandExecutor(this, lang).register("delwarp");
        new WarpTabCompleter(this).register("warp", "delwarp");
    }

    private void loadPlayerFetcher() {
        this.playerFetcher = new PlayerFetcher(this);

        playerFetcher.load(createKeysFile());
    }

    private File createKeysFile() {
        return new File(getDataFolder(), "keys.json");
    }

    private void loadTeleporter(Configuration config) {
        this.teleporter = new PlaceTeleporter(this, lang);

        teleporter.setMaxDelay(config.getInt("max-delay"));
        teleporter.setMovementAllowed(config.getBoolean("movement-allowed"));
        teleporter.setDamageAllowed(config.getBoolean("damage-allowed"));
        teleporter.modCommandBlocker(config.getInt("cmd-blocker.mode"), config.getStringList("cmd-blocker.list"));
    }
}
