package me.fabiogvdneto.places.common.i18n;

import me.fabiogvdneto.places.common.Plugins;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginTranslator extends AbstractTranslator {

    private String code;

    public PluginTranslator() {
        super(new HashMap<>());
    }

    @Override
    public String code() {
        return code;
    }

    public void clearTranslations() {
        translations.clear();
    }

    public void loadTranslations(Plugin plugin, String code) {
        String path = path(code);
        plugin.getLogger().info("Loading message translations (" + path + ")...");
        Configuration config = Plugins.loadConfiguration(plugin, path);

        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            if (entry.getValue().getClass() == String.class) {
                translations.put(entry.getKey(), (String) entry.getValue());
            }
        }

        this.code = code;
    }

    private String path(String code) {
        return "messages" + File.separatorChar + code + ".yml";
    }
}