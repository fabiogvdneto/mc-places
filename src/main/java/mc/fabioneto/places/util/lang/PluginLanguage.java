package mc.fabioneto.places.util.lang;

import com.google.common.base.Preconditions;
import mc.fabioneto.places.util.Resources;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PluginLanguage implements Language {

    public final static String EXT = ".yml";
    public final static String FALLBACK = "en";

    private final Plugin plugin;
    private final String prefix;
    private String code;

    private Map<String, String> translations;

    /**
     * @param plugin Plugin that contains the language resources.
     * @param prefix Prefix for language files.
     */
    public PluginLanguage(Plugin plugin, String prefix) {
        this.plugin = Objects.requireNonNull(plugin);
        this.prefix = prefix.stripTrailing();

        Preconditions.checkArgument(!prefix.isEmpty());
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Message translate(String key) {
        Preconditions.checkNotNull(code, "Language not loaded yet.");

        return new Message(translations.getOrDefault(key, ""));
    }

    public void load(String code) {
        load(code, FALLBACK);
    }

    public void load(String code, String fallback) {
        this.code = Objects.requireNonNull(code);

        Configuration config = loadConfig(code);

        if ((fallback != null) && !(fallback.equals(code))) {
            config.setDefaults(loadConfig(fallback));
        }

        this.translations = new HashMap<>();

        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                String translation = config.getString(key);

                translations.put(key, ChatColor.translateAlternateColorCodes('&', translation));
            }
        }

        this.translations = Collections.unmodifiableMap(translations);
    }

    private Configuration loadConfig(String code) {
        return Resources.saveAndLoad(plugin, (prefix + code + EXT));
    }
}
